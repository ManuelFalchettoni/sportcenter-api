package com.tpfinal.sportcenter_api.repository.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA de turnos.
 */
@Repository
public interface JpaAppointmentRepository extends
        JpaRepository<Appointment, Long>,
        JpaSpecificationExecutor<Appointment> {

    /**
     * Indica si el profesional ya tiene un turno que se superpone con el rango
     * [startTime, endTime) recibido. El sufijo StatusNot deja afuera los turnos
     * en el estado excluido (CANCELLED): un turno cancelado libera el horario.
     */
    boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
            Long professionalId, LocalDateTime endTime, LocalDateTime startTime,
            AppointmentStatusEnum excludedStatus);

    /**
     * excluye un turno por id. Se usa al actualizar, para que el propio turno
     * que se está editando no cuente como un solapamiento consigo mismo.
     */
    boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndStatusNot(
            Long professionalId, LocalDateTime endTime, LocalDateTime startTime, Long id,
            AppointmentStatusEnum excludedStatus);

    /**
     * Indica si el usuario ya tiene un turno (con cualquier profesional) que se
     * superpone con el rango [startTime, endTime) recibido: una persona no
     * puede estar en dos turnos a la misma hora.
     */
    boolean existsByUserIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
            Long userId, LocalDateTime endTime, LocalDateTime startTime,
            AppointmentStatusEnum excludedStatus);

    /**
     * Variante para el update: excluye al propio turno que se está editando.
     */
    boolean existsByUserIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndStatusNot(
            Long userId, LocalDateTime endTime, LocalDateTime startTime, Long id,
            AppointmentStatusEnum excludedStatus);

    /**
     * Turnos de un usuario, paginados. Lo usa el listado cuando el caller
     * no es ADMIN: cada uno ve solo sus propios turnos.
     */
    Page<Appointment> findByUserId(Long userId, Pageable pageable);

    /**
     * Turnos activos del profesional que pisan el rango [startTime, endTime),
     * ordenados por hora de inicio. Misma condición de solapamiento que los
     * exists de arriba, pero devolviendo los turnos: lo usa la consulta de
     * disponibilidad para listar los horarios ocupados de un día.
     */
    List<Appointment> findByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNotOrderByStartTimeAsc(
            Long professionalId, LocalDateTime endTime, LocalDateTime startTime,
            AppointmentStatusEnum excludedStatus);
}
