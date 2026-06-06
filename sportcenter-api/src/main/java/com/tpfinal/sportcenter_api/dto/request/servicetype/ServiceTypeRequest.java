package com.tpfinal.sportcenter_api.dto.request.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ServiceTypeRequest {
    @NotBlank
    @Size(min = 3, max = 80)
    private String name;
    @NotNull
    @Positive
    @Max(480)
    private Integer durationMinutes;
    @NotNull
    @PositiveOrZero
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    public ServiceTypeRequest(){};
    public ServiceTypeRequest(String name, Integer durationMinutes, BigDecimal price) {
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
