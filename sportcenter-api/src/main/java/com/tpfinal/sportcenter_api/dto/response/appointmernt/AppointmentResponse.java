package com.tpfinal.sportcenter_api.dto.response.appointmernt;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;

import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long id;
    private String clientName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean confirmed;
    private String notes;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private Long professionalId;
    private String professionalName;
    private Long serviceTypeId;
    private String serviceTypeName;

    public AppointmentResponse(){}

    public AppointmentResponse(Long id, String clientName, LocalDateTime startTime, LocalDateTime endTime, Boolean confirmed, String notes, LocalDateTime createdAt, Long userId, String username, Long professionalId, String professionalName, Long serviceTypeId, String serviceTypeName) {
        this.id = id;
        this.clientName = clientName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confirmed = confirmed;
        this.notes = notes;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.serviceTypeId = serviceTypeId;
        this.serviceTypeName = serviceTypeName;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public static AppointmentResponse toResponse(Appointment appointment){
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getClientName(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getConfirmed(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUser().getId(),
                appointment.getUser().getUsername(),
                appointment.getProfessional().getId(),
                appointment.getProfessional().getName(),
                appointment.getServiceType().getId(),
                appointment.getServiceType().getName()
        );
    }
}
