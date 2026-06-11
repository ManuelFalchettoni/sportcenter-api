package com.tpfinal.sportcenter_api.service.auth;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.request.auth.LoginRequest;
import com.tpfinal.sportcenter_api.dto.response.auth.LoginResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.auth.InvalidCredentialsException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Autentica por email + password y emite un JWT.
 * El token usa username como subject y el rol como claim.
 */
@Service
public class LoginService {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Hash BCrypt "señuelo" usado cuando el email no existe. Sirve para que el
    // login tarde lo mismo exista o no el usuario: así un atacante no puede
    // deducir qué emails están registrados midiendo el tiempo de respuesta.
    // Se genera una vez al construir el bean, con el mismo encoder
    private final String dummyHash;

    public LoginService(JpaUserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing-mitigation");
    }

    /**
     * Valida las credenciales y, si son correctas, devuelve un token.
     */
    public LoginResponse login(LoginRequest request) {
        // Normalizamos igual que en el registro para que el lookup matchee.
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Email inexistente: igualamos el costo verificando contra el hash
            // señuelo (descartamos el resultado) y devolvemos el error genérico.
            passwordEncoder.matches(request.getPassword(), dummyHash);
            throw new InvalidCredentialsException();
        }

        // matches(raw, encoded) compara la password en claro contra el hash BCrypt.
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token, jwtService.getExpirationSeconds());
    }
}
