package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Fábrica de Specifications para filtrar turnos dinámicamente.
 *
 * Cada método devuelve una condición atómica (un WHERE parcial); el servicio
 * combina con AND solo las que aplican según los filtros recibidos. Es el
 * mecanismo que aprovecha el JpaSpecificationExecutor del repositorio: Spring
 * arma la consulta con Criteria API en lugar de un derived query por cada
 * combinación posible de filtros.
 */
public final class AppointmentSpecifications {

    private AppointmentSpecifications() {
        // Solo métodos estáticos: no se instancia.
    }

    /** Turnos cuyo dueño es el usuario dado. Es la regla de ownership del listado. */
    public static Specification<Appointment> ownedBy(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    /** Turnos que empiezan en from o después (inclusive). */
    public static Specification<Appointment> startsAtOrAfter(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startTime"), from);
    }

    /** Turnos que empiezan en to o antes (inclusive). */
    public static Specification<Appointment> startsAtOrBefore(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startTime"), to);
    }

    /** Turnos en el estado dado. */
    public static Specification<Appointment> hasStatus(AppointmentStatusEnum status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /** Turnos con el profesional dado. */
    public static Specification<Appointment> withProfessional(Long professionalId) {
        return (root, query, cb) -> cb.equal(root.get("professional").get("id"), professionalId);
    }

    /**
     * Turnos cuyas notas, nombre del profesional, nombre del tipo de servicio o
     * username del dueño contienen el texto dado (case-insensitive). Es la
     * búsqueda libre del listado: cubre los campos de texto que un usuario
     * reconoce de un turno. El username solo discrimina para un ADMIN (que ve
     * turnos de varios usuarios); para un USER todos los turnos son propios.
     */
    public static Specification<Appointment> matchesQuery(String text) {
        return (root, query, cb) -> {
            String like = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("notes")), like),
                    cb.like(cb.lower(root.get("professional").get("name")), like),
                    cb.like(cb.lower(root.get("serviceType").get("name")), like),
                    cb.like(cb.lower(root.get("user").get("username")), like)
            );
        };
    }
}
