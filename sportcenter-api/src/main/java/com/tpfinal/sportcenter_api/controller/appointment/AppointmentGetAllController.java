package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el listado paginado de turnos.
 * Ruta base: /sportcenter/appointments.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentGetAllController {
    private final AppointmentGetAllService appointmentGetAllService;

    public AppointmentGetAllController(AppointmentGetAllService appointmentGetAllService) {
        this.appointmentGetAllService = appointmentGetAllService;
    }

    /**
     * Lista turnos en forma paginada. Un ADMIN ve todos; un USER los propios.
     */
    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> findAll(Pageable pageable,
                                                             @AuthenticationPrincipal UserPrincipal principal) {
        Page<AppointmentResponse> response = appointmentGetAllService.findAll(pageable, principal.getUser())
                .map(AppointmentResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
