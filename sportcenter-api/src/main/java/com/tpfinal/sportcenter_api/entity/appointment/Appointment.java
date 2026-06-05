package com.tpfinal.sportcenter_api.entity.appointment;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank
    private String clientName;

    @Column
    @NotNull
    private LocalDateTime startTime;

    @Column
    @NotNull
    private LocalDateTime endTime;

    @Column
    @NotNull
    private Boolean confirmed;

    @Column
    @NotBlank
    private String notes;

    @Column
    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "service_type_ id", nullable = false)
    private ServiceType serviceType;

    public Appointment(){}

    public Appointment(Long id, String clientName, LocalDateTime startTime, LocalDateTime endTime, Boolean confirmed, String notes, LocalDateTime createdAt, User user, Professional professional, ServiceType serviceType) {
        this.id = id;
        this.clientName = clientName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confirmed = confirmed;
        this.notes = notes;
        this.createdAt = createdAt;
        this.user = user;
        this.professional = professional;
        this.serviceType = serviceType;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
}
