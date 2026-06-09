package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de eliminar turnos existentes.
 */
@Service
public class AppointmentDeleterService {
    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final AppointmentFinderService appointmentFinderService;
    private final AppointmentOwnershipValidator ownershipValidator;

    public AppointmentDeleterService(JpaAppointmentRepository jpaAppointmentRepository,
                                     AppointmentFinderService appointmentFinderService,
                                     AppointmentOwnershipValidator ownershipValidator) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.appointmentFinderService = appointmentFinderService;
        this.ownershipValidator = ownershipValidator;
    }

    /**
     * Elimina el turno identificado por el ID recibido.
     * Si no existe, el finder lanza AppointmentNotFoundException.
     * Solo el dueño del turno o un ADMIN pueden eliminarlo.
     */
    public void delete(Long id, User caller) {
        Appointment appointment = appointmentFinderService.find(id);
        ownershipValidator.check(appointment, caller);
        jpaAppointmentRepository.delete(appointment);
    }
}
