package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de actualizar turnos existentes.
 * Solo el dueño del turno o un ADMIN pueden actualizarlo. El dueño no se
 * modifica en el update: un turno no se transfiere a otro usuario.
 */
@Service
public class AppointmentUpdaterService {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final JpaServiceTypeRepository jpaServiceTypeRepository;
    private final AppointmentFinderService appointmentFinderService;
    private final AppointmentOwnershipValidator ownershipValidator;
    private final AppointmentOverlapValidator overlapValidator;

    public AppointmentUpdaterService(JpaAppointmentRepository jpaAppointmentRepository,
                                     JpaProfessionalRepository jpaProfessionalRepository,
                                     JpaServiceTypeRepository jpaServiceTypeRepository,
                                     AppointmentFinderService appointmentFinderService,
                                     AppointmentOwnershipValidator ownershipValidator,
                                     AppointmentOverlapValidator overlapValidator) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
        this.appointmentFinderService = appointmentFinderService;
        this.ownershipValidator = ownershipValidator;
        this.overlapValidator = overlapValidator;
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
        // este mismo turno (si solo cambian las notas, no debe chocar consigo
        // mismo). El eje usuario se valida contra el dueño del turno, no contra
        // el caller, que puede ser un ADMIN editando un turno ajeno.
        overlapValidator.checkForUpdate(request.getProfessionalId(), appointment.getUser().getId(),
                request.getStartTime(), request.getEndTime(), id);

        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setNotes(request.getNotes());
        appointment.setProfessional(professional);
        appointment.setServiceType(serviceType);

        Appointment updated = jpaAppointmentRepository.save(appointment);
        return AppointmentResponse.toResponse(updated);
    }
}
