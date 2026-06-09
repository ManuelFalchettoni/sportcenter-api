package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone la consulta de un turno por ID.
 * Ruta base: /sportcenter/appointments.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentGetController {
    private final AppointmentFinderService appointmentFinderService;

    public AppointmentGetController(AppointmentFinderService appointmentFinderService) {
        this.appointmentFinderService = appointmentFinderService;
    }

    /**
     * Obtiene un turno por su ID. Solo el dueño o un ADMIN.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> find(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserPrincipal principal){
        Appointment appointment = appointmentFinderService.find(id, principal.getUser());
        return ResponseEntity.ok(AppointmentResponse.toResponse(appointment));
    }
}
