package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio que expone el listado paginado de turnos.
 * Un ADMIN ve todos los turnos; un USER solo los propios.
 */
@Service
public class AppointmentGetAllService {
    private final JpaAppointmentRepository jpaAppointmentRepository;

    public AppointmentGetAllService(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }
    /**
     * Devuelve la página de turnos solicitada, filtrada según el caller.
     */
    public Page<Appointment> findAll(Pageable pageable, User caller) {
        if (caller.getRole() == UserEnum.ADMIN) {
            return jpaAppointmentRepository.findAll(pageable);
        }
        return jpaAppointmentRepository.findByUserId(caller.getId(), pageable);
    }
}
