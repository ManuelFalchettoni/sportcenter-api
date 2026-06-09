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
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de actualizar turnos existentes.
 * Reemplaza horario, notas y entidades relacionadas (profesional y tipo de
 * servicio), validando previamente que el rango horario sea coherente.
 *
 * <p>Solo el dueño del turno o un ADMIN pueden actualizarlo. El dueño no se
 * modifica en el update: un turno no se transfiere a otro usuario.
 */
@Service
public class AppointmentUpdaterService {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final JpaServiceTypeRepository jpaServiceTypeRepository;
    private final AppointmentFinderService appointmentFinderService;
    private final AppointmentOwnershipValidator ownershipValidator;

    public AppointmentUpdaterService(JpaAppointmentRepository jpaAppointmentRepository,
                                     JpaProfessionalRepository jpaProfessionalRepository,
                                     JpaServiceTypeRepository jpaServiceTypeRepository,
                                     AppointmentFinderService appointmentFinderService,
                                     AppointmentOwnershipValidator ownershipValidator) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
        this.appointmentFinderService = appointmentFinderService;
        this.ownershipValidator = ownershipValidator;
    }

    /**
     * Actualiza los datos de un turno existente del caller (o de cualquiera
     * si el caller es ADMIN).
     */
    public AppointmentResponse update(Long id, @Valid AppointmentRequest request, User caller) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }

        Appointment appointment = appointmentFinderService.find(id);
        ownershipValidator.check(appointment, caller);

        Professional professional = jpaProfessionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new ProfessionalNotFoundException(request.getProfessionalId()));
        ServiceType serviceType = jpaServiceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new ServiceTypeNotFoundException(request.getServiceTypeId()));

        // Misma protección de doble reserva que en la creación, pero excluyendo
        // este mismo turno (si solo cambian las notas, no debe chocar consigo mismo).
        if (jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndCancelledFalse(
                request.getProfessionalId(), request.getEndTime(), request.getStartTime(), id)) {
            throw new AppointmentOverlapException(request.getProfessionalId());
        }

        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setNotes(request.getNotes());
        appointment.setProfessional(professional);
        appointment.setServiceType(serviceType);

        Appointment updated = jpaAppointmentRepository.save(appointment);
        return AppointmentResponse.toResponse(updated);
    }
}
