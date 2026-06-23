package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que expone el listado paginado de usuarios, con búsqueda opcional
 * por texto sobre username y email (query general) o por campo individual.
 */
@Service
public class UserGetAllService {
    private final JpaUserRepository jpaUserRepository;

    public UserGetAllService(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    /**
     * Devuelve la página de usuarios solicitada. Los filtros opcionales se
     * combinan con AND (los null/vacíos no filtran):
     * - query: coincidencia (case-insensitive) sobre username o email.
     * - username: coincidencia (case-insensitive) sobre username.
     * - email: coincidencia (case-insensitive) sobre email.
     */
    public Page<User> findAll(Pageable pageable, String query, String username, String email) {
        List<Specification<User>> specs = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            specs.add(UserSpecifications.matchesQuery(query.trim()));
        }
        if (username != null && !username.isBlank()) {
            specs.add(UserSpecifications.usernameContains(username.trim()));
        }
        if (email != null && !email.isBlank()) {
            specs.add(UserSpecifications.emailContains(email.trim()));
        }
        // allOf combina con AND; con la lista vacía no restringe nada.
        return jpaUserRepository.findAll(Specification.allOf(specs), pageable);
    }
}
