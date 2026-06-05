package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de actualizar usuarios existentes.
 * <p>
 * Valida unicidad de username/email cuando cambian respecto al valor actual
 * y rehashea la contraseña solo si se provee una nueva no vacía.
 */
@Service
public class UserUpdaterService {
    private final JpaUserRepository jpaUserRepository;
    private final UserFinderService userFinderService;
    private final PasswordEncoder passwordEncoder;

    public UserUpdaterService(JpaUserRepository jpaUserRepository, UserFinderService userFinderService, PasswordEncoder passwordEncoder) {
        this.jpaUserRepository = jpaUserRepository;
        this.userFinderService = userFinderService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Actualiza los datos del usuario identificado por el ID.
     *
     * @param id identificador del usuario a actualizar.
     * @param request nuevos datos del usuario; la contraseña se actualiza solo si no es nula ni vacía.
     * @return DTO de respuesta con el estado actualizado del usuario.
     * @throws com.tpfinal.sportcenter_api.exception.user.UserNotFoundException si no existe.
     * @throws UserAlreadyExistsException si el nuevo username o email ya están en uso por otro usuario.
     */
    public UserResponse update(Long id, @Valid UserRequest request) {
        User user = userFinderService.find(id);

        if (!user.getUsername().equals(request.getUsername())
                && jpaUserRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }
        if (!user.getEmail().equals(request.getEmail())
                && jpaUserRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = jpaUserRepository.save(user);
        return UserResponse.toResponse(updated);
    }
}
