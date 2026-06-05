package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.entity.user.User;
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
        if (jpaUserRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }
        if (jpaUserRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }
        User user = UserRequest.fromRequest(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        return jpaUserRepository.save(user);
    }
}
