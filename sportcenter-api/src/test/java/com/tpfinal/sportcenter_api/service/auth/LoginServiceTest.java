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

// Activa Mockito para procesar los @Mock antes de cada test.
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    // Dependencias del LoginService como mocks.
    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder; // compara/hashea contraseñas
    @Mock
    private JwtService jwtService;            // genera el token

    // Acá NO usamos @InjectMocks: el servicio se crea a mano en setUp() porque
    // su constructor necesita que el encoder ya esté "stubbeado" (ver abajo).
    private LoginService service;

    private User user;

    @BeforeEach
    void setUp() {
        // El constructor de LoginService llama a encode(...) para crear un hash
        // "señuelo". Por eso stubbeamos esa llamada ANTES de construir el servicio.
        when(passwordEncoder.encode("dummy-password-for-timing-mitigation")).thenReturn("$2a$10$dummyhash");
        service = new LoginService(userRepository, passwordEncoder, jwtService);

        // Usuario de ejemplo con un hash "real" simulado.
        user = new User(1L, "juan", "juan@mail.com", "$2a$10$realhash", UserEnum.USER, LocalDateTime.now());
    }

    // Caso feliz: email existe y la contraseña coincide -> devuelve el token.
    @Test
    void login_returnsTokenWhenCredentialsAreValid() {
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        // matches(plano, hash) = true significa "la contraseña es correcta".
        when(passwordEncoder.matches("secret123", "$2a$10$realhash")).thenReturn(true);
        when(jwtService.generateToken("juan", UserEnum.USER)).thenReturn("signed.jwt.token");

        // Mandamos el email con espacios y mayúsculas a propósito: el servicio
        // lo normaliza (trim + lowercase) y por eso el lookup de arriba matchea.
        LoginResponse response = service.login(new LoginRequest("  Juan@Mail.com ", "secret123"));

        assertThat(response.getToken()).isEqualTo("signed.jwt.token");
    }

    // Email inexistente -> error genérico (no revela si el email existe).
    @Test
    void login_throwsWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("ghost@mail.com", "secret123")))
                .isInstanceOf(InvalidCredentialsException.class);

        // Aunque el usuario no exista, igual compara contra el hash señuelo para
        // que el login tarde lo mismo (mitigación de timing). eq(...) son matchers
        // que exigen esos valores exactos en la llamada verificada.
        verify(passwordEncoder).matches(eq("secret123"), eq("$2a$10$dummyhash"));
        // Y nunca debe generar token si falló la autenticación.
        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    // Contraseña incorrecta -> mismo error genérico, sin token.
    @Test
    void login_throwsWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$10$realhash")).thenReturn(false); // no coincide

        assertThatThrownBy(() -> service.login(new LoginRequest("juan@mail.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
