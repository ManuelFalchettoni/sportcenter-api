package com.tpfinal.sportcenter_api.enums.appointment;

/**
 * Estado del turno. Reemplaza a los flags confirmed/cancelled: un turno nace
 * PENDING, puede pasar a CONFIRMED y CANCELLED actúa como soft delete (el
 * turno queda en el historial pero libera el horario del profesional).
 */
public enum AppointmentStatusEnum {
    PENDING,
    CONFIRMED,
    CANCELLED
}
