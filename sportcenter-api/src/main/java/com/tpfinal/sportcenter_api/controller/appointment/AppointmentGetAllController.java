package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sportcenter/apoointments")
public class AppointmentGetAllController {
    private final AppointmentGetAllService appointmentGetAllService;

    public AppointmentGetAllController(AppointmentGetAllService appointmentGetAllService) {
        this.appointmentGetAllService = appointmentGetAllService;
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> findAll(Pageable pageable) {
        Page<AppointmentResponse> response = appointmentGetAllService.findAll(pageable)
                .map(AppointmentResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
