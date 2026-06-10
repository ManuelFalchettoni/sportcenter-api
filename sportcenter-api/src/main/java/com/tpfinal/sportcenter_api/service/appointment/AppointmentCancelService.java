package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentAlreadyCancelledException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de cancelar turnos existentes.
 * marca el turno como cancelado y
 * registra el momento, dejándolo en el historial. El turno cancelado deja de
 * ocupar el horario del profesional
 */
@Service
public class AppointmentCancelService {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final AppointmentFinderService appointmentFinderService;
    private final AppointmentOwnershipValidator ownershipValidator;

    public AppointmentCancelService(JpaAppointmentRepository jpaAppointmentRepository,
                                    AppointmentFinderService appointmentFinderService,
                                    AppointmentOwnershipValidator ownershipValidator) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.appointmentFinderService = appointmentFinderService;
        this.ownershipValidator = ownershipValidator;
    }

    /**
     * Cancela el turno identificado por el ID recibido.
     * Solo el dueño del turno o un ADMIN pueden cancelarlo.
     */
    public AppointmentResponse cancel(Long id, User caller) {
        Appointment appointment = appointmentFinderService.find(id);
        ownershipValidator.check(appointment, caller);

        if (appointment.getStatus() == AppointmentStatusEnum.CANCELLED) {
            throw new AppointmentAlreadyCancelledException(id);
        }

        appointment.setStatus(AppointmentStatusEnum.CANCELLED);
        appointment.setStatusModifiedAt(LocalDateTime.now());

        Appointment cancelled = jpaAppointmentRepository.save(appointment);
        return AppointmentResponse.toResponse(cancelled);
    }
}
