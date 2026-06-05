package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentCreatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentPostController {
    private final AppointmentCreatorService appointmentCreatorService;

    public AppointmentPostController(AppointmentCreatorService appointmentCreatorService) {
        this.appointmentCreatorService = appointmentCreatorService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request){
        Appointment appointment = appointmentCreatorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResponse.toResponse(appointment));
    }
}
