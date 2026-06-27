package com.tpfinal.sportcenter_api.entity.professional;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professionals")
public class Professional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String speciality;
    @NotNull
    @Column(nullable = false)
    private Boolean active;
    @Size(max = 500)
    @Column(length = 500)
    private String photoUrl;

    //creacion de tabla relacional. LAZY carga los servicios solo si los solicito con el get
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "professional_service_types",
            joinColumns = @JoinColumn(name = "professional_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id")
    )
    private Set<ServiceType> services = new HashSet<>(); // En los SET no se permiten datos duplicados

    public Professional(){};
    public Professional(String name, String speciality, Boolean active) {
        this.name = name;
        this.speciality = speciality;
        this.active = active;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Set<ServiceType> getServices() {
        return services;
    }

    public void setServices(Set<ServiceType> services) {
        this.services = services;
    }
}
