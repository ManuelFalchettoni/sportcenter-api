package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la actualización de turnos.
 * Ruta base: /sportcenter/appointments.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentPutController {
    private final AppointmentUpdaterService appointmentUpdaterService;

    public AppointmentPutController(AppointmentUpdaterService appointmentUpdaterService) {
        this.appointmentUpdaterService = appointmentUpdaterService;
    }

    /**
     * Actualiza un turno existente. Solo el dueño o un ADMIN.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody AppointmentRequest request,
                                                      @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentResponse response = appointmentUpdaterService.update(id, request, principal.getUser());
        return ResponseEntity.ok(response);
    }
}
