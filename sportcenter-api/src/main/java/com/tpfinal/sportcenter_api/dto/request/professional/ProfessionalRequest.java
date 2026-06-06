package com.tpfinal.sportcenter_api.dto.request.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class ProfessionalRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;
    @NotBlank
    @Size(min = 3, max = 50)
    private String speciality;
    @NotNull
    private Boolean active;

    // Cada ID debe ser positivo y no nulo. Capear a 50 evita payloads abusivos
    // (un profesional con cientos de servicios distintos no tiene sentido funcional).
    @Size(max = 50)
    private Set<@NotNull @Positive Long> serviceTypeIds = new HashSet<>();

    public ProfessionalRequest(){};
    public ProfessionalRequest(String name, String speciality, Boolean active, Set<Long> serviceTypeIds) {
        this.name = name;
        this.speciality = speciality;
        this.active = active;
        this.serviceTypeIds = serviceTypeIds != null ? serviceTypeIds : new HashSet<>();
    }

    static public Professional fromRequest (ProfessionalRequest request){
        return new Professional(
                request.getName(),
                request.getSpeciality(),
                request.getActive()
        );
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

    public Set<Long> getServiceTypeIds() {
        return serviceTypeIds;
    }

    public void setServiceTypeIds(Set<Long> serviceTypeIds) {
        this.serviceTypeIds = serviceTypeIds != null ? serviceTypeIds : new HashSet<>();
    }
}
