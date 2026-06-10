package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotPendingException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de confirmar turnos pendientes (PENDING -> CONFIRMED).
 *
 * La confirmación es la aceptación de la reserva por parte del centro, por eso
 * el endpoint es solo-ADMIN (el dueño ya expresó su intención al crear el
 * turno). Solo un turno PENDING puede confirmarse: confirmar dos veces o
 * confirmar uno cancelado responde 409.
 */
@Service
public class AppointmentConfirmService {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final AppointmentFinderService appointmentFinderService;

    public AppointmentConfirmService(JpaAppointmentRepository jpaAppointmentRepository,
                                     AppointmentFinderService appointmentFinderService) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.appointmentFinderService = appointmentFinderService;
    }

    /**
     * Confirma el turno identificado por el ID recibido y registra el momento
     * del cambio de estado en statusModifiedAt.
     */
    public AppointmentResponse confirm(Long id) {
        Appointment appointment = appointmentFinderService.find(id);

        if (appointment.getStatus() != AppointmentStatusEnum.PENDING) {
            throw new AppointmentNotPendingException(id, appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatusEnum.CONFIRMED);
        appointment.setStatusModifiedAt(LocalDateTime.now());

        Appointment confirmed = jpaAppointmentRepository.save(appointment);
        return AppointmentResponse.toResponse(confirmed);
    }
}
