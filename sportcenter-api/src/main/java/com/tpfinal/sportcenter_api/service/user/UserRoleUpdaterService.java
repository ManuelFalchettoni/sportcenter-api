package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRoleUpdateRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio administrativo encargado de actualizar el rol de un usuario.
 * <p>
 * Está separado del flujo público ({@link UserUpdaterService}) para garantizar
 * que el cambio de rol solo pueda realizarse a través de un endpoint dedicado
 * (que en el futuro estará restringido a ADMIN).
 */
@Service
public class UserRoleUpdaterService {

    private final JpaUserRepository jpaUserRepository;
    private final UserFinderService userFinderService;

    public UserRoleUpdaterService(JpaUserRepository jpaUserRepository,
                                  UserFinderService userFinderService) {
        this.jpaUserRepository = jpaUserRepository;
        this.userFinderService = userFinderService;
    }

    /**
     * Cambia el rol del usuario identificado por el ID.
     *
     * @param id identificador del usuario.
     * @param request DTO con el nuevo rol.
     * @return DTO de respuesta con el usuario actualizado.
     * @throws com.tpfinal.sportcenter_api.exception.user.UserNotFoundException
     *         si el usuario no existe.
     */
    public UserResponse updateRole(Long id, UserRoleUpdateRequest request) {
        User user = userFinderService.find(id);
        user.setRole(request.getRole());
        User updated = jpaUserRepository.save(user);
        return UserResponse.toResponse(updated);
    }
}
