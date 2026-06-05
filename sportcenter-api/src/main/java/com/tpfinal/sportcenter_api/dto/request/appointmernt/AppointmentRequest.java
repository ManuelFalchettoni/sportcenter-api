package com.tpfinal.sportcenter_api.dto.request.appointmernt;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AppointmentRequest {

    @NotBlank
    private String clientName;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Boolean confirmed;

    @NotBlank
    private String notes;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private Long userId;

    @NotNull
    private Long professionalId;

    @NotNull
    private Long serviceTypeId;

    public AppointmentRequest(){}

    public AppointmentRequest(String clientName, LocalDateTime startTime, LocalDateTime endTime, Boolean confirmed, String notes, LocalDateTime createdAt, Long userId, Long professionalId, Long serviceTypeId) {
        this.clientName = clientName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confirmed = confirmed;
        this.notes = notes;
        this.createdAt = createdAt;
        this.userId = userId;
        this.professionalId = professionalId;
        this.serviceTypeId = serviceTypeId;
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

    public static Appointment fromRequest(AppointmentRequest request){
        Appointment appointment = new Appointment();
        appointment.setClientName(request.getClientName());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setConfirmed(request.getConfirmed());
        appointment.setNotes(request.getNotes());
        appointment.setCreatedAt(request.getCreatedAt());

        User user = new User();
        user.setId(request.getUserId());
        appointment.setUser(user);

        Professional professional = new Professional();
        professional.setId(request.getProfessionalId());
        appointment.setProfessional(professional);

        ServiceType serviceType = new ServiceType();
        serviceType.setId(request.getServiceTypeId());
        appointment.setServiceType(serviceType);
        return  appointment;
    }
}
