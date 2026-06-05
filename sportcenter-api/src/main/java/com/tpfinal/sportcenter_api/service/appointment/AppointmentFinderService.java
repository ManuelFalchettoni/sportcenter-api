package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AppointmentFinderService {
    private final JpaAppointmentRepository jpaAppointmentRepository;

    public AppointmentFinderService(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }

    public Appointment find(Long id){
        Appointment appointment = jpaAppointmentRepository.findById(id)
                .orElseThrow(()-> new AppointmentNotFoundException(id));
        return appointment;
    }
}


