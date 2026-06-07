package com.tpfinal.sportcenter_api.service.auth;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.request.auth.LoginRequest;
import com.tpfinal.sportcenter_api.dto.response.auth.LoginResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.auth.InvalidCredentialsException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private LoginService service;

    private User user;

    @BeforeEach
    void setUp() {
        // El constructor llama a passwordEncoder.encode(...) para el hash señuelo.
        when(passwordEncoder.encode("dummy-password-for-timing-mitigation")).thenReturn("$2a$10$dummyhash");
        service = new LoginService(userRepository, passwordEncoder, jwtService);

        user = new User(1L, "juan", "juan@mail.com", "$2a$10$realhash", UserEnum.USER, LocalDateTime.now());
    }

    @Test
    void login_returnsTokenWhenCredentialsAreValid() {
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "$2a$10$realhash")).thenReturn(true);
        when(jwtService.generateToken("juan", UserEnum.USER)).thenReturn("signed.jwt.token");

        // El email se normaliza (trim + lowercase) antes del lookup.
        LoginResponse response = service.login(new LoginRequest("  Juan@Mail.com ", "secret123"));

        assertThat(response.getToken()).isEqualTo("signed.jwt.token");
    }

    @Test
    void login_throwsWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("ghost@mail.com", "secret123")))
                .isInstanceOf(InvalidCredentialsException.class);

        // Mitigación de timing: igual verifica contra el hash señuelo.
        verify(passwordEncoder).matches(eq("secret123"), eq("$2a$10$dummyhash"));
        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void login_throwsWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$10$realhash")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequest("juan@mail.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
