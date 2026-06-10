package com.tpfinal.sportcenter_api.dto.response.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;

import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatusEnum status;
    private LocalDateTime statusModifiedAt;
    private String notes;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private Long professionalId;
    private String professionalName;
    private Long serviceTypeId;
    private String serviceTypeName;

    public AppointmentResponse(){}

    public AppointmentResponse(Long id, LocalDateTime startTime, LocalDateTime endTime, AppointmentStatusEnum status,
                               LocalDateTime statusModifiedAt, String notes, LocalDateTime createdAt,
                               Long userId, String username,
                               Long professionalId, String professionalName, Long serviceTypeId, String serviceTypeName) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.statusModifiedAt = statusModifiedAt;
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

    public AppointmentStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getStatusModifiedAt() {
        return statusModifiedAt;
    }

    public void setStatusModifiedAt(LocalDateTime statusModifiedAt) {
        this.statusModifiedAt = statusModifiedAt;
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
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getStatusModifiedAt(),
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
