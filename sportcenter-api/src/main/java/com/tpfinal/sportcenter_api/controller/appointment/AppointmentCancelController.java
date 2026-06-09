package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentCancelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * Cancela un turno existente sin eliminarlo. Solo el dueño o un ADMIN.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentResponse response = appointmentCancelService.cancel(id, principal.getUser());
        return ResponseEntity.ok(response);
    }
}
