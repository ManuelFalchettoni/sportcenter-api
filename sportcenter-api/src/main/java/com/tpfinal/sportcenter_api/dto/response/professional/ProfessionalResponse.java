package com.tpfinal.sportcenter_api.dto.response.professional;


import com.tpfinal.sportcenter_api.entity.professional.Professional;

public class ProfessionalResponse {
    private Long id;
    private String name;
    private String speciality;
    private Boolean active;

    public ProfessionalResponse(){};
    public ProfessionalResponse(Long id, String name, String speciality, Boolean active) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.active = active;
    }

    static public ProfessionalResponse toResponse(Professional professional){
        return new ProfessionalResponse(
                professional.getId(),
                professional.getName(),
                professional.getSpeciality(),
                professional.getActive()
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
}
