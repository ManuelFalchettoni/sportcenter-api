package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de actualizar usuarios existentes.
 * <p>
 * Valida unicidad de username/email cuando cambian respecto al valor actual
 * y rehashea la contraseña solo si se provee una nueva no vacía.
 * Cuando un no-ADMIN cambia su propia contraseña, debe confirmar la vigente
 * (currentPassword): un token robado no alcanza para tomar la cuenta.
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
     * El caller (ya autorizado por el @PreAuthorize del controller: ADMIN o el
     * propio usuario) se usa para decidir si exigir la contraseña vigente.
     */
    public UserResponse update(Long id, @Valid UserRequest request, User caller) {
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
            // Un no-ADMIN cambiando su clave debe confirmar la vigente. Un ADMIN
            // puede resetear sin ella (flujo de "olvidé mi contraseña" vía admin).
            if (caller.getRole() != UserEnum.ADMIN) {
                if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                    throw new IllegalArgumentException("currentPassword is required to change the password");
                }
                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                    throw new IllegalArgumentException("currentPassword is incorrect");
                }
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = jpaUserRepository.save(user);
        return UserResponse.toResponse(updated);
    }
}
