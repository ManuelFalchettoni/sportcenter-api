package com.tpfinal.sportcenter_api.service.auth;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.request.auth.LoginRequest;
import com.tpfinal.sportcenter_api.dto.response.auth.LoginResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.exception.auth.UserNotFoundByEmailException;
import com.tpfinal.sportcenter_api.exception.auth.WrongPasswordException;
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

    public LoginService(JpaUserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        // Normalizamos igual que en el registro para que el lookup matchee.
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException(email));

        // matches(raw, encoded) compara la password en claro contra el hash BCrypt.
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token);
    }
}
