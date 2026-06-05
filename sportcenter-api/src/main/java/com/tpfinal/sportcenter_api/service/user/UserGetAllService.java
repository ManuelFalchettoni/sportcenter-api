package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio que expone el listado paginado de usuarios.
 */
@Service
public class UserGetAllService {
    private final JpaUserRepository jpaUserRepository;

    public UserGetAllService(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    /**
     * Devuelve la página de usuarios solicitada.
     *
     * @param pageable parámetros de paginación y ordenamiento.
     * @return página con los usuarios correspondientes.
     */
    public Page<User> findAll(Pageable pageable) {
        return jpaUserRepository.findAll(pageable);
    }
}
