package com.tpfinal.sportcenter_api.repository.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repositorio JPA de turnos.
 *
 * <p>Es una <b>interfaz sin implementación escrita a mano</b>: Spring Data, al
 * arrancar, genera un proxy que la implementa. Los métodos de abajo son
 * <i>derived query methods</i>: Spring arma la consulta a partir del <b>nombre</b>
 * del método —cada palabra clave ({@code ProfessionalId}, {@code StartTimeBefore},
 * {@code EndTimeAfter}, {@code IdNot}) se traduce en una condición del WHERE—, por
 * eso no tienen cuerpo. El detalle de cómo se detecta el solapamiento está en el README.
 */
@Repository
public interface JpaAppointmentRepository extends
        JpaRepository<Appointment, Long>,
        JpaSpecificationExecutor<Appointment> {

    /**
     * Indica si el profesional ya tiene un turno que se superpone con el rango
     * [startTime, endTime) recibido.
     *
     * <p>Dos rangos se solapan cuando uno empieza antes de que el otro termine y
     * termina después de que el otro empieza. Traducido a esta consulta:
     * {@code turno.startTime < endTime AND turno.endTime > startTime}. Se usan
     * comparaciones estrictas a propósito: dos turnos que apenas se tocan (uno
     * termina justo cuando el otro empieza) NO se consideran solapados.
     *
     * @param professionalId profesional a verificar.
     * @param endTime   fin del rango nuevo (se compara contra el inicio de los turnos existentes).
     * @param startTime inicio del rango nuevo (se compara contra el fin de los turnos existentes).
     * @return {@code true} si existe al menos un turno solapado.
     */
    boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfter(
            Long professionalId, LocalDateTime endTime, LocalDateTime startTime);

    /**
     * Igual que {@link #existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfter},
     * pero excluye un turno por id. Se usa al actualizar, para que el propio turno
     * que se está editando no cuente como un solapamiento consigo mismo.
     *
     * @param id identificador del turno que se está actualizando (se excluye de la búsqueda).
     */
    boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNot(
            Long professionalId, LocalDateTime endTime, LocalDateTime startTime, Long id);
}
