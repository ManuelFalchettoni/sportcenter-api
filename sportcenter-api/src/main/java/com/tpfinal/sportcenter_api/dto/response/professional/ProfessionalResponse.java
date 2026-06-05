package com.tpfinal.sportcenter_api.dto.response.professional;


import com.tpfinal.sportcenter_api.dto.response.servicetype.ServiceTypeResponse;
import com.tpfinal.sportcenter_api.entity.professional.Professional;

import java.util.Set;
import java.util.stream.Collectors;

public class ProfessionalResponse {
    private Long id;
    private String name;
    private String speciality;
    private Boolean active;
    private Set<ServiceTypeResponse> services;

    public ProfessionalResponse(){};
    public ProfessionalResponse(Long id, String name, String speciality, Boolean active, Set<ServiceTypeResponse> services) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.active = active;
        this.services = services;
    }

    static public ProfessionalResponse toResponse(Professional professional){
        Set<ServiceTypeResponse> services = professional.getServices() == null
                ? Set.of()
                : professional.getServices().stream()
                    .map(ServiceTypeResponse::toResponse)
                    .collect(Collectors.toSet());
        return new ProfessionalResponse(
                professional.getId(),
                professional.getName(),
                professional.getSpeciality(),
                professional.getActive(),
                services
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<ServiceTypeResponse> getServices() {
        return services;
    }

    public void setServices(Set<ServiceTypeResponse> services) {
        this.services = services;
    }
}
