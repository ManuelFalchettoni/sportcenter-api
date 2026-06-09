package com.tpfinal.sportcenter_api.repository.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repositorio JPA de turnos.
 */
@Repository
public interface JpaAppointmentRepository extends
        JpaRepository<Appointment, Long>,
        JpaSpecificationExecutor<Appointment> {

    /**
     * Indica si el profesional ya tiene un turno que se superpone con el rango
     * [startTime, endTime) recibido. El sufijo CancelledFalse deja afuera los
     * turnos cancelados: un turno cancelado libera el horario.
     */
    boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndCancelledFalse(
            Long professionalId, LocalDateTime endTime, LocalDateTime startTime);

    /**
     * excluye un turno por id. Se usa al actualizar, para que el propio turno
     * que se está editando no cuente como un solapamiento consigo mismo.
     */
    boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndCancelledFalse(
            Long professionalId, LocalDateTime endTime, LocalDateTime startTime, Long id);
}
