package com.tpfinal.sportcenter_api.dto.request.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProfessionalRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String speciality;
    @NotNull
    private Boolean active;

    public ProfessionalRequest(){};
    public ProfessionalRequest(String name, String speciality, Boolean active) {
        this.name = name;
        this.speciality = speciality;
        this.active = active;
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
}
