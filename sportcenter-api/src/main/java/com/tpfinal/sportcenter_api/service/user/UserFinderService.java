package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.user.UserNotFoundException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio de consulta que recupera un usuario individual por su ID.
 * Centraliza el manejo de "no encontrado" para otros servicios.
 */
@Service
public class UserFinderService {
    private final JpaUserRepository jpaUserRepository;

    public UserFinderService(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario.
     * @return el usuario correspondiente.
     * @throws com.tpfinal.sportcenter_api.exception.user.UserNotFoundException
     *         si no existe.
     */
    public User find(Long id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
