package com.tpfinal.sportcenter_api.exception.professional;

/**
 * Se lanza al intentar borrar un profesional que tiene turnos asociados
 * (en cualquier estado): el DELETE violaría la FK y perdería historial.
 * La alternativa es desactivarlo (active = false), que además bloquea
 * reservas nuevas sin tocar los turnos existentes.
 */
public class ProfessionalHasAppointmentsException extends RuntimeException {

    public ProfessionalHasAppointmentsException(Long id) {
        super("The professional with id: " + id + " cannot be deleted because it has appointments. "
                + "Deactivate it (active = false) instead.");
    }
}
