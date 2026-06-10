package com.tpfinal.sportcenter_api.exception.professional;

/**
 * Se lanza al intentar dirigir una reserva a un profesional inactivo:
 * crear un turno con él, o mover un turno existente hacia él.
 */
public class ProfessionalInactiveException extends RuntimeException {

    public ProfessionalInactiveException(Long id) {
        super("The professional with id: " + id + " is inactive and cannot take new appointments.");
    }
}
