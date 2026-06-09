package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentCancelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la cancelación de turnos.
 * Ruta base:  /sportcenter/appointments.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentCancelController {
    private final AppointmentCancelService appointmentCancelService;

    public AppointmentCancelController(AppointmentCancelService appointmentCancelService) {
        this.appointmentCancelService = appointmentCancelService;
    }

    /**
     * Cancela un turno existente sin eliminarlo.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        AppointmentResponse response = appointmentCancelService.cancel(id);
        return ResponseEntity.ok(response);
    }
}
