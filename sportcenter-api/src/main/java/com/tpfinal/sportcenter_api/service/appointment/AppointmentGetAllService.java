package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio que expone el listado paginado de turnos.
 */
@Service
public class AppointmentGetAllService {
    private final JpaAppointmentRepository jpaAppointmentRepository;

    public AppointmentGetAllService(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }
    /**
     * Devuelve la página de turnos solicitada.
     *
     * @param pageable parámetros de paginación y ordenamiento.
     * @return página con los turnos correspondientes.
     */
    public Page<Appointment> findAll(Pageable pageable) {
        return jpaAppointmentRepository.findAll(pageable);
    }
}
