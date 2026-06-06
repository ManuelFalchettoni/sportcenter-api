package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
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
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de actualizar turnos existentes.
 * <p>
 * Reemplaza horario, notas y entidades relacionadas (usuario, profesional
 * y tipo de servicio), validando previamente que el rango horario sea coherente.
 */
@Service
public class AppointmentUpdaterService {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final JpaServiceTypeRepository jpaServiceTypeRepository;
    private final AppointmentFinderService appointmentFinderService;

    public AppointmentUpdaterService(JpaAppointmentRepository jpaAppointmentRepository,
                                     JpaUserRepository jpaUserRepository,
                                     JpaProfessionalRepository jpaProfessionalRepository,
                                     JpaServiceTypeRepository jpaServiceTypeRepository,
                                     AppointmentFinderService appointmentFinderService) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
        this.appointmentFinderService = appointmentFinderService;
    }

    /**
     * Actualiza los datos de un turno existente.
     *
     * @param id identificador del turno a actualizar.
     * @param request nuevos datos del turno.
     * @return DTO de respuesta con el estado actualizado del turno.
     * @throws IllegalArgumentException si {@code endTime} no es posterior a {@code startTime}.
     * @throws com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException si el turno no existe.
     * @throws com.tpfinal.sportcenter_api.exception.user.UserNotFoundException si el usuario no existe.
     * @throws com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException si el profesional no existe.
     * @throws com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException si el tipo de servicio no existe.
     * @throws com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException si el profesional ya tiene otro turno que se solapa con el rango pedido.
     */
    public AppointmentResponse update(Long id, @Valid AppointmentRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }

        Appointment appointment = appointmentFinderService.find(id);

        User user = jpaUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        Professional professional = jpaProfessionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new ProfessionalNotFoundException(request.getProfessionalId()));
        ServiceType serviceType = jpaServiceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new ServiceTypeNotFoundException(request.getServiceTypeId()));

        // Misma protección de doble reserva que en la creación, pero excluyendo
        // este mismo turno (si solo cambian las notas, no debe chocar consigo mismo).
        if (jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNot(
                request.getProfessionalId(), request.getEndTime(), request.getStartTime(), id)) {
            throw new AppointmentOverlapException(request.getProfessionalId());
        }

        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setNotes(request.getNotes());
        appointment.setUser(user);
        appointment.setProfessional(professional);
        appointment.setServiceType(serviceType);

        Appointment updated = jpaAppointmentRepository.save(appointment);
        return AppointmentResponse.toResponse(updated);
    }
}
