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
     */
    public Page<Appointment> findAll(Pageable pageable, User caller, AppointmentFilterRequest filter) {
        if (filter.getFrom() != null && filter.getTo() != null && filter.getFrom().isAfter(filter.getTo())) {
            throw new IllegalArgumentException("'from' must be before or equal to 'to'");
        }

        List<Specification<Appointment>> specs = new ArrayList<>();

        // Ownership primero: un USER jamás ve turnos ajenos, filtre lo que filtre.
        if (caller.getRole() != UserEnum.ADMIN) {
            specs.add(AppointmentSpecifications.ownedBy(caller.getId()));
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
