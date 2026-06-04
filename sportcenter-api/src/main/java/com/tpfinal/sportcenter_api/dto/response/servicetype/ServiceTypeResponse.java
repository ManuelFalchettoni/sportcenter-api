package com.tpfinal.sportcenter_api.dto.response.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;

import java.math.BigDecimal;

public class ServiceTypeResponse {
    private Long id;
    private String name;
    private int durationMinutes;
    private BigDecimal price;

    public ServiceTypeResponse(){};
    public ServiceTypeResponse(Long id, String name, int durationMinutes, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    static public ServiceTypeResponse toResponse(ServiceType serviceType){
        return new ServiceTypeResponse(
                serviceType.getId(),
                serviceType.getName(),
                serviceType.getDurationMinutes(),
                serviceType.getPrice()
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
