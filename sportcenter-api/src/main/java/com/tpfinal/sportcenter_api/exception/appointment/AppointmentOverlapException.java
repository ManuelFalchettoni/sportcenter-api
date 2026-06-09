package com.tpfinal.sportcenter_api.exception.appointment;

/**
 * Se lanza al crear o actualizar un turno cuyo horario se superpone con otro
 * turno ya existente del mismo profesional (doble reserva).
 * El status (409 Conflict) y el body los asigna GlobalExceptionHandler.
 */
public class AppointmentOverlapException extends RuntimeException {

    public AppointmentOverlapException(Long professionalId) {
        super("The professional with id: " + professionalId
                + " already has an appointment that overlaps the requested time range.");
    }
}
