package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentFilterRequest;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que expone el listado paginado de turnos, con filtros opcionales
 * por rango de fechas (sobre startTime), estado y profesional.
 * Un ADMIN ve todos los turnos; un USER solo los propios: la regla de
 * ownership es una Specification más, siempre presente para no-ADMIN.
 */
@Service
public class AppointmentGetAllService {
    private final JpaAppointmentRepository jpaAppointmentRepository;

    public AppointmentGetAllService(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }

    /**
     * Devuelve la página de turnos solicitada, filtrada según el caller y los
     * filtros opcionales recibidos (los null no filtran; se combinan con AND).
     * Un ADMIN ve todos los turnos; un USER solo los propios.
     */
    public Page<Appointment> findAll(Pageable pageable, User caller, AppointmentFilterRequest filter) {
        // Ownership solo para no-ADMIN: el ADMIN ve los turnos de todos.
        Long ownerId = caller.getRole() == UserEnum.ADMIN ? null : caller.getId();
        return find(pageable, ownerId, filter);
    }

    /**
     * Devuelve la página de turnos del usuario autenticado, sin importar su rol.
     * A diferencia de findAll, acota siempre al dueño: es la vista "mis turnos",
     * para que un ADMIN también pueda ver su propia agenda y no la del centro.
     */
    public Page<Appointment> findMine(Pageable pageable, User owner, AppointmentFilterRequest filter) {
        return find(pageable, owner.getId(), filter);
    }

    /**
     * Núcleo compartido del listado: arma las Specifications según los filtros y
     * ejecuta la consulta paginada. Si ownerId no es null, acota a ese dueño
     * (regla de ownership); si es null no restringe por usuario (ADMIN ve todo).
     */
    private Page<Appointment> find(Pageable pageable, Long ownerId, AppointmentFilterRequest filter) {
        if (filter.getFrom() != null && filter.getTo() != null && filter.getFrom().isAfter(filter.getTo())) {
            throw new IllegalArgumentException("'from' must be before or equal to 'to'");
        }

        List<Specification<Appointment>> specs = new ArrayList<>();

        // Ownership primero: un USER jamás ve turnos ajenos, filtre lo que filtre.
        if (ownerId != null) {
            specs.add(AppointmentSpecifications.ownedBy(ownerId));
        }
        if (filter.getFrom() != null) {
            specs.add(AppointmentSpecifications.startsAtOrAfter(filter.getFrom()));
        }
        if (filter.getTo() != null) {
            specs.add(AppointmentSpecifications.startsAtOrBefore(filter.getTo()));
        }
        if (filter.getStatus() != null) {
            specs.add(AppointmentSpecifications.hasStatus(filter.getStatus()));
        }
        if (filter.getProfessionalId() != null) {
            specs.add(AppointmentSpecifications.withProfessional(filter.getProfessionalId()));
        }
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            specs.add(AppointmentSpecifications.matchesQuery(filter.getQuery().trim()));
        }

        // allOf combina con AND; con la lista vacía (ADMIN sin filtros) no restringe nada.
        return jpaAppointmentRepository.findAll(Specification.allOf(specs), pageable);
    }
}
