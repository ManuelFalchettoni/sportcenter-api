package com.tpfinal.sportcenter_api.entity.servicetype;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "service_types")
public class ServiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 3, max = 80)
    @Column(nullable = false, length = 80)
    private String name;
    @NotNull
    @Positive
    @Max(480)
    @Column(nullable = false)
    private Integer durationMinutes;
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal price;

    public ServiceType(){};
    public ServiceType(String name, Integer durationMinutes, BigDecimal price) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
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
