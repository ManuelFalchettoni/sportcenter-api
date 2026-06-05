package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la actualización de turnos.
 * Ruta base: {@code /sportcenter/appointments}.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentPutController {
    private final AppointmentUpdaterService appointmentUpdaterService;

    public AppointmentPutController(AppointmentUpdaterService appointmentUpdaterService) {
        this.appointmentUpdaterService = appointmentUpdaterService;
    }

    /**
     * Actualiza un turno existente.
     *
     * @param id identificador del turno a actualizar.
     * @param request datos validados con el nuevo estado del turno.
     * @return 200 OK con el turno actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentUpdaterService.update(id, request);
        return ResponseEntity.ok(response);
    }
}
