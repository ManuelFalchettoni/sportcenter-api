package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import org.springframework.data.jpa.domain.Specification;

/**
 * Fábrica de Specifications para filtrar profesionales dinámicamente.
 *
 * Cada método devuelve una condición atómica (un WHERE parcial); el servicio
 * combina con AND solo las que aplican según los filtros recibidos. Aprovecha
 * el JpaSpecificationExecutor del repositorio para armar la consulta con
 * Criteria API en lugar de un derived query por combinación de filtros.
 */
public final class ProfessionalSpecifications {

    private ProfessionalSpecifications() {
        // Solo métodos estáticos: no se instancia.
    }

    /**
     * Profesionales cuyo nombre o especialidad contienen el texto dado
     * (case-insensitive).
     */
    public static Specification<Professional> matchesQuery(String query) {
        return (root, criteriaQuery, cb) -> {
            String like = "%" + query.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("speciality")), like)
            );
        };
    }

    /** Profesionales cuyo nombre contiene el texto dado (case-insensitive). */
    public static Specification<Professional> nameContains(String name) {
        return fieldContains("name", name);
    }

    /** Profesionales cuya especialidad contiene el texto dado (case-insensitive). */
    public static Specification<Professional> specialityContains(String speciality) {
        return fieldContains("speciality", speciality);
    }

    private static Specification<Professional> fieldContains(String field, String value) {
        return (root, criteriaQuery, cb) ->
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }
}
