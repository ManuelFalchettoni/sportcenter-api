package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.entity.user.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * Fábrica de Specifications para filtrar usuarios dinámicamente.
 *
 * Cada método devuelve una condición atómica (un WHERE parcial); el servicio
 * combina con AND solo las que aplican según los filtros recibidos. Aprovecha
 * el JpaSpecificationExecutor del repositorio para armar la consulta con
 * Criteria API en lugar de un derived query por combinación de filtros.
 */
public final class UserSpecifications {

    private UserSpecifications() {
        // Solo métodos estáticos: no se instancia.
    }

    /**
     * Usuarios cuyo username o email contienen el texto dado (case-insensitive).
     */
    public static Specification<User> matchesQuery(String query) {
        return (root, criteriaQuery, cb) -> {
            String like = "%" + query.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("username")), like),
                    cb.like(cb.lower(root.get("email")), like)
            );
        };
    }

    /** Usuarios cuyo username contiene el texto dado (case-insensitive). */
    public static Specification<User> usernameContains(String username) {
        return fieldContains("username", username);
    }

    /** Usuarios cuyo email contiene el texto dado (case-insensitive). */
    public static Specification<User> emailContains(String email) {
        return fieldContains("email", email);
    }

    private static Specification<User> fieldContains(String field, String value) {
        return (root, criteriaQuery, cb) ->
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }
}
