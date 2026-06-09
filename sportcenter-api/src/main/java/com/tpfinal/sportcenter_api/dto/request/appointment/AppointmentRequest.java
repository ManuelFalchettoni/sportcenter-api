package com.tpfinal.sportcenter_api.dto.request.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AppointmentRequest {

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    @Size(max = 500)
    private String notes;

    // El dueño del turno no viaja en el body: se toma del usuario autenticado
    // (UserPrincipal). Así nadie puede reservar a nombre de otro.

    @NotNull
    @Positive
    private Long professionalId;

    @NotNull
    @Positive
    private Long serviceTypeId;

    public AppointmentRequest(){}

    public AppointmentRequest(LocalDateTime startTime, LocalDateTime endTime, String notes,
                              Long professionalId, Long serviceTypeId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
        this.professionalId = professionalId;
        this.serviceTypeId = serviceTypeId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

}
