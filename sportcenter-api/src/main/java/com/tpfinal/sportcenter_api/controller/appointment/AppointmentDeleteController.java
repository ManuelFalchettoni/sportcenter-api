package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.service.appointment.AppointmentDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sportcenter/appointments")
public class AppointmentDeleteController {
    private final AppointmentDeleterService appointmentDeleterService;

    public AppointmentDeleteController(AppointmentDeleterService appointmentDeleterService) {
        this.appointmentDeleterService = appointmentDeleterService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentDeleterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
