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
     */
    public UserResponse update(Long id, @Valid UserRequest request) {
        User user = userFinderService.find(id);

        // Normalización: trim en username y trim+lowercase en email,
        // para que la unicidad y comparación sean consistentes.
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (!user.getUsername().equals(username)
                && jpaUserRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("username", username);
        }
        if (!user.getEmail().equals(email)
                && jpaUserRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("email", email);
        }

        user.setUsername(username);
        user.setEmail(email);
        // El rol no se actualiza desde este endpoint: se mantiene el valor actual.
        // Cualquier cambio de rol debe hacerse por un endpoint admin separado.

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = jpaUserRepository.save(user);
        return UserResponse.toResponse(updated);
    }
}
