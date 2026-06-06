package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.exception.user.UserNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de crear nuevos turnos (appointments).
 * <p>
 * Valida el rango horario recibido y resuelve las entidades asociadas
 * (usuario, profesional y tipo de servicio) antes de persistir el turno.
 */
@Service
public class AppointmentCreatorService {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public AppointmentCreatorService(JpaAppointmentRepository jpaAppointmentRepository,
                                     JpaUserRepository jpaUserRepository,
                                     JpaProfessionalRepository jpaProfessionalRepository,
                                     JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    /**
     * Crea y persiste un nuevo turno a partir de los datos recibidos.
     * <p>
     * El turno se guarda como no confirmado y con la fecha de creación
     * establecida en el momento actual.
     *
     * @param request datos del turno a crear (horario, notas e IDs relacionados).
     * @return el turno persistido con su ID generado.
     * @throws IllegalArgumentException si {@code endTime} no es posterior a {@code startTime}.
     * @throws UserNotFoundException si el usuario no existe.
     * @throws ProfessionalNotFoundException si el profesional no existe.
     * @throws ServiceTypeNotFoundException si el tipo de servicio no existe.
     * @throws AppointmentOverlapException si el profesional ya tiene un turno que se solapa con el rango pedido.
     */
    public Appointment create(AppointmentRequest request){
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }

        User user = jpaUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        Professional professional = jpaProfessionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new ProfessionalNotFoundException(request.getProfessionalId()));
        ServiceType serviceType = jpaServiceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new ServiceTypeNotFoundException(request.getServiceTypeId()));

        // Evitamos la doble reserva: si el profesional ya tiene un turno que se
        // solapa con el rango pedido, no permitimos crear otro.
        if (jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfter(
                request.getProfessionalId(), request.getEndTime(), request.getStartTime())) {
            throw new AppointmentOverlapException(request.getProfessionalId());
        }

        Appointment appointment = new Appointment(
                request.getStartTime(),
                request.getEndTime(),
                request.getNotes(),
                user,
                professional,
                serviceType
        );
        appointment.setConfirmed(false);
        appointment.setCreatedAt(LocalDateTime.now());

        return jpaAppointmentRepository.save(appointment);
    }
}
