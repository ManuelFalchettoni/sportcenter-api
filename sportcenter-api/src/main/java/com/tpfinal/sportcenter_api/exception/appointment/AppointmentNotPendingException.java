package com.tpfinal.sportcenter_api.exception.appointment;

import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;

/**
 * Se lanza al intentar confirmar un turno que no está en estado PENDING:
 * uno ya confirmado (operación repetida) o uno cancelado (la cancelación
 * es definitiva; para retomar el horario se crea un turno nuevo).
 */
public class AppointmentNotPendingException extends RuntimeException {

    public AppointmentNotPendingException(Long id, AppointmentStatusEnum status) {
        super("The appointment with id: " + id + " cannot be confirmed because its status is "
                + status + " (only PENDING appointments can be confirmed).");
    }
}
