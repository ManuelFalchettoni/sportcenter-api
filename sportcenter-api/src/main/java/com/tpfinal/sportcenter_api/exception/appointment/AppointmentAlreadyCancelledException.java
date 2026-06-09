package com.tpfinal.sportcenter_api.exception.appointment;

/**
 * Se lanza al intentar cancelar un turno que ya estaba cancelado.
 */
public class AppointmentAlreadyCancelledException extends RuntimeException {

    public AppointmentAlreadyCancelledException(Long id) {
        super("The appointment with id: " + id + " is already cancelled.");
    }
}
