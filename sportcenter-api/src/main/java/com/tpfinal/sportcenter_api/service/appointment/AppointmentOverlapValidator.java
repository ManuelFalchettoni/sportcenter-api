package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.appointment.UserAppointmentOverlapException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Regla de no-solapamiento de turnos, en sus dos ejes:
 * - un profesional no puede tener dos turnos activos que se pisen, y
 * - un usuario tampoco, aunque sean con profesionales distintos.
 * Los turnos CANCELLED no cuentan: un turno cancelado libera el horario.
 */
@Component
public class AppointmentOverlapValidator {

    private final JpaAppointmentRepository jpaAppointmentRepository;

    public AppointmentOverlapValidator(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }

    /**
     * Valida el rango pedido al crear un turno. Tira AppointmentOverlapException
     * si pisa otro turno del profesional, o UserAppointmentOverlapException si
     * pisa otro turno del dueño.
     */
    public void checkForCreate(Long professionalId, Long ownerId,
                               LocalDateTime startTime, LocalDateTime endTime) {
        if (jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
                professionalId, endTime, startTime, AppointmentStatusEnum.CANCELLED)) {
            throw new AppointmentOverlapException(professionalId);
        }
        if (jpaAppointmentRepository.existsByUserIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
                ownerId, endTime, startTime, AppointmentStatusEnum.CANCELLED)) {
            throw new UserAppointmentOverlapException(ownerId);
        }
    }

    /**
     * Variante para el update: excluye al propio turno que se está editando,
     * para que no choque consigo mismo si solo cambian las notas.
     */
    public void checkForUpdate(Long professionalId, Long ownerId,
                               LocalDateTime startTime, LocalDateTime endTime, Long appointmentId) {
        if (jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndStatusNot(
                professionalId, endTime, startTime, appointmentId, AppointmentStatusEnum.CANCELLED)) {
            throw new AppointmentOverlapException(professionalId);
        }
        if (jpaAppointmentRepository.existsByUserIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndStatusNot(
                ownerId, endTime, startTime, appointmentId, AppointmentStatusEnum.CANCELLED)) {
            throw new UserAppointmentOverlapException(ownerId);
        }
    }
}
