package com.tpfinal.sportcenter_api.dto.request.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class ServiceTypeRequest {
    @NotBlank
    private String name;
    @Positive
    private int durationMinutes;
    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    public ServiceTypeRequest(){};
    public ServiceTypeRequest(String name, int durationMinutes, BigDecimal price) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    static public ServiceType fromRequest(ServiceTypeRequest request){
        return new ServiceType(
                request.getName(),
                request.getDurationMinutes(),
                request.getPrice()
        );
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
