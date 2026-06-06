package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de registrar nuevos usuarios.
 * <p>
 * Verifica unicidad de username y email, hashea la contraseña con
 * {@link PasswordEncoder} y establece la fecha de creación.
 */
@Service
public class UserCreatorService {
    private final JpaUserRepository jpaUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreatorService(JpaUserRepository jpaUserRepository, PasswordEncoder passwordEncoder) {
        this.jpaUserRepository = jpaUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea y persiste un nuevo usuario.
     *
     * @param request datos del usuario a registrar (incluye contraseña en claro).
     * @return el usuario persistido con su ID generado y contraseña hasheada.
     * @throws com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException
     *         si ya existe un usuario con el mismo username o email.
     */
    public User create(UserRequest request) {
        // Normalización: trim en username y trim+lowercase en email,
        // para que la unicidad no dependa de espacios o capitalización.
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (jpaUserRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("username", username);
        }
        if (jpaUserRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("email", email);
        }
        User user = UserRequest.fromRequest(request);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // El rol nunca se acepta desde el body para evitar auto-escalada de privilegios:
        // todo registro queda como USER. Cambios de rol se harán por un endpoint admin.
        user.setRole(UserEnum.USER);
        user.setCreatedDate(LocalDateTime.now());
        return jpaUserRepository.save(user);
    }
}
