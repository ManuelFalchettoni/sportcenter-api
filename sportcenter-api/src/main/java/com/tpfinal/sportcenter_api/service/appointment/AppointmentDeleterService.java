package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de eliminar turnos existentes.
 */
@Service
public class AppointmentDeleterService {
    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final AppointmentFinderService appointmentFinderService;

    public AppointmentDeleterService(JpaAppointmentRepository jpaAppointmentRepository,
                                     AppointmentFinderService appointmentFinderService) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.appointmentFinderService = appointmentFinderService;
    }

    /**
     * Elimina el turno identificado por el ID recibido.
     * Si no existe, el finder lanza AppointmentNotFoundException.
     */
    public void delete(Long id) {
        Appointment appointment = appointmentFinderService.find(id);
        jpaAppointmentRepository.delete(appointment);
    }
}
