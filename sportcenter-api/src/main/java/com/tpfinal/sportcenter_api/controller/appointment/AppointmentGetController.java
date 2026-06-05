package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentGetController {
    private final AppointmentFinderService appointmentFinderService;

    public AppointmentGetController(AppointmentFinderService appointmentFinderService) {
        this.appointmentFinderService = appointmentFinderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> find(@PathVariable Long id){
        Appointment appointment = appointmentFinderService.find(id);
        return ResponseEntity.ok(AppointmentResponse.toResponse(appointment));
    }
}
