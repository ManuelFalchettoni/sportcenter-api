package com.tpfinal.sportcenter_api.service.appointmernt;

import com.tpfinal.sportcenter_api.dto.request.appointmernt.AppointmentRequest;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Service;

@Service
public class AppointmentCreatorService {
    private final JpaAppointmentRepository jpaAppointmentRepository;


    public AppointmentCreatorService(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }

    public Appointment create(AppointmentRequest request){
        Appointment appointment = AppointmentRequest.fromRequest(request);
        Appointment created = jpaAppointmentRepository.save(appointment);
        return created;
    }
}
