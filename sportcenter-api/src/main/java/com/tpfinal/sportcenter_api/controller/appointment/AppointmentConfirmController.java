package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentConfirmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone la confirmación de turnos.
 * Ruta base: /sportcenter/appointments.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentConfirmController {
    private final AppointmentConfirmService appointmentConfirmService;

    public AppointmentConfirmController(AppointmentConfirmService appointmentConfirmService) {
        this.appointmentConfirmService = appointmentConfirmService;
    }

    /**
     * Confirma un turno pendiente (PENDING -> CONFIRMED). Solo ADMIN: la
     * confirmación es la aceptación de la reserva por parte del centro.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable Long id) {
        AppointmentResponse response = appointmentConfirmService.confirm(id);
        return ResponseEntity.ok(response);
    }
}
