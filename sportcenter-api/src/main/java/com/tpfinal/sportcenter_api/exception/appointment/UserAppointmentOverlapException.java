package com.tpfinal.sportcenter_api.exception.appointment;

/**
 * Se lanza al crear o actualizar un turno cuyo horario se superpone con otro
 * turno ya existente del mismo usuario (una persona no puede tener dos turnos
 * a la misma hora, aunque sean con profesionales distintos).
 * El status (409 Conflict) y el body los asigna GlobalExceptionHandler.
 */
public class UserAppointmentOverlapException extends RuntimeException {

    public UserAppointmentOverlapException(Long userId) {
        super("The user with id: " + userId
                + " already has an appointment that overlaps the requested time range.");
    }
}
