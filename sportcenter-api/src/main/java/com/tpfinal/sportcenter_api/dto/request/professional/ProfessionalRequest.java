package com.tpfinal.sportcenter_api.dto.request.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;

public class ProfessionalRequest {
    private String name;
    private String speciality;
    private boolean active;

    public ProfessionalRequest(){};
    public ProfessionalRequest(String name, String speciality, boolean active) {
        this.name = name;
        this.speciality = speciality;
        this.active = active;
    }

    static public Professional fromRequest (ProfessionalRequest request){
        return new Professional(
                request.getName(),
                request.getSpeciality(),
                request.isActive()
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
