package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio de consulta que recupera un turno individual por su ID.
 * <p>
 * Es reutilizado por otros servicios (eliminación y actualización) para
 * centralizar el manejo de "no encontrado". Esos services hacen su propio
 * chequeo de ownership, por eso find(Long) no lo aplica; la variante con
 * caller la usa el controller del GET.
 */
@Service
public class AppointmentFinderService {
    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final AppointmentOwnershipValidator ownershipValidator;

    public AppointmentFinderService(JpaAppointmentRepository jpaAppointmentRepository,
                                    AppointmentOwnershipValidator ownershipValidator) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.ownershipValidator = ownershipValidator;
    }

    /**
     * Busca un turno por su identificador, sin chequeo de ownership.
     * Uso interno de otros services.
     */
    public Appointment find(Long id){
        Appointment appointment = jpaAppointmentRepository.findById(id)
                .orElseThrow(()-> new AppointmentNotFoundException(id));
        return appointment;
    }

    /**
     * Busca un turno por su identificador para el caller: solo el dueño
     * o un ADMIN pueden verlo.
     */
    public Appointment find(Long id, User caller){
        Appointment appointment = find(id);
        ownershipValidator.check(appointment, caller);
        return appointment;
    }
}


