package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
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

    public AppointmentCancelService(JpaAppointmentRepository jpaAppointmentRepository,
                                    AppointmentFinderService appointmentFinderService) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.appointmentFinderService = appointmentFinderService;
    }

    /**
     * Cancela el turno identificado por el ID recibido.
     */
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = appointmentFinderService.find(id);

        if (Boolean.TRUE.equals(appointment.getCancelled())) {
            throw new AppointmentAlreadyCancelledException(id);
        }

        appointment.setCancelled(true);
        appointment.setCancelledAt(LocalDateTime.now());

        Appointment cancelled = jpaAppointmentRepository.save(appointment);
        return AppointmentResponse.toResponse(cancelled);
    }
}
