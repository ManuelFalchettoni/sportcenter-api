package com.tpfinal.sportcenter_api.dto.response.professional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Horarios ocupados de un profesional en un día dado.
 *
 * Expone únicamente los rangos horarios (startTime/endTime): nada de quién
 * reservó, notas ni ids de turnos. Así un USER puede ver la agenda para elegir
 * un horario libre sin acceder a datos de otros usuarios.
 */
public class ProfessionalAvailabilityResponse {

    private Long professionalId;
    private LocalDate date;
    private List<BusySlot> busySlots;

    public ProfessionalAvailabilityResponse() {}

    public ProfessionalAvailabilityResponse(Long professionalId, LocalDate date, List<BusySlot> busySlots) {
        this.professionalId = professionalId;
        this.date = date;
        this.busySlots = busySlots;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<BusySlot> getBusySlots() {
        return busySlots;
    }

    public void setBusySlots(List<BusySlot> busySlots) {
        this.busySlots = busySlots;
    }

    /** Un rango horario ocupado. Sin ninguna otra información del turno. */
    public static class BusySlot {

        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public BusySlot() {}

        public BusySlot(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
    }
}
