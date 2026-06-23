package com.tpfinal.sportcenter_api.dto.request.appointment;

import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;

import java.time.LocalDateTime;

/**
 * Filtros opcionales del listado de turnos. Todos los campos son nullable:
 * un campo en null significa "no filtrar por esto", y se combinan con AND.
 * Lo arma el controller desde los query params (no es un @RequestBody).
 */
public class AppointmentFilterRequest {

    /** Turnos que empiezan en este momento o después (inclusive). */
    private final LocalDateTime from;

    /** Turnos que empiezan en este momento o antes (inclusive). */
    private final LocalDateTime to;

    private final AppointmentStatusEnum status;

    private final Long professionalId;

    /** Búsqueda libre sobre notas, nombre del profesional o del tipo de servicio. */
    private final String query;

    public AppointmentFilterRequest(LocalDateTime from, LocalDateTime to,
                                    AppointmentStatusEnum status, Long professionalId,
                                    String query) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.professionalId = professionalId;
        this.query = query;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public AppointmentStatusEnum getStatus() {
        return status;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public String getQuery() {
        return query;
    }
}
