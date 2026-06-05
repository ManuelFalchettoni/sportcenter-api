package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.exception.user.UserNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

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
