package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio de consulta que recupera un turno individual por su ID.
 * <p>
 * Es reutilizado por otros servicios (eliminación y actualización) para
 * centralizar el manejo de "no encontrado".
 */
@Service
public class AppointmentFinderService {
    private final JpaAppointmentRepository jpaAppointmentRepository;

    public AppointmentFinderService(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }

    /**
     * Busca un turno por su identificador.
     *
     * @param id identificador del turno.
     * @return el turno correspondiente.
     * @throws com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException
     *         si el turno no existe.
     */
    public Appointment find(Long id){
        Appointment appointment = jpaAppointmentRepository.findById(id)
                .orElseThrow(()-> new AppointmentNotFoundException(id));
        return appointment;
    }
}


