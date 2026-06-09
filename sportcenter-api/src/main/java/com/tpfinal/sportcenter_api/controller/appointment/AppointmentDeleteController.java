package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la baja de turnos.
 * Ruta base: /sportcenter/appointments.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentDeleteController {
    private final AppointmentDeleterService appointmentDeleterService;

    public AppointmentDeleteController(AppointmentDeleterService appointmentDeleterService) {
        this.appointmentDeleterService = appointmentDeleterService;
    }

    /**
     * Elimina el turno indicado. Solo el dueño o un ADMIN.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        appointmentDeleterService.delete(id, principal.getUser());
        return ResponseEntity.noContent().build();
    }
}
