package com.tpfinal.sportcenter_api.config;

import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

// Sin @ExtendWith(Mockito): JwtService no tiene dependencias que mockear, lo
// instanciamos directo y le seteamos sus campos de configuración a mano.
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // En la app, "secret" y "expirationMs" se inyectan desde application.properties
        // con @Value. En el test los inyectamos por reflexión (ReflectionTestUtils)
        // porque son privados y no hay setters.
        // HS256 exige una clave de al menos 32 bytes (256 bits).
        ReflectionTestUtils.setField(jwtService, "secret", "clave-de-prueba-con-mas-de-32-caracteres-1234567890");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3_600_000L); // 1 hora
    }

    // Un token recién generado debe ser no vacío y válido.
    @Test
    void generateToken_producesValidToken() {
        String token = jwtService.generateToken("juan", UserEnum.USER);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isValid(token)).isTrue();
    }

    // El token debe poder convertirse en un Authentication con el username como
    // principal y el rol expuesto como authority "ROLE_ADMIN".
    @Test
    void toAuthentication_exposesUsernameAndRoleAuthority() {
        String token = jwtService.generateToken("juan", UserEnum.ADMIN);

        Authentication auth = jwtService.toAuthentication(token);

        assertThat(auth.getPrincipal()).isEqualTo("juan");
        // extracting: de cada authority sacamos su String y verificamos que
        // entre ellas esté "ROLE_ADMIN" (el prefijo ROLE_ lo exige Spring Security).
        assertThat(auth.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }

    // Texto basura o vacío no son tokens válidos.
    @Test
    void isValid_returnsFalseForGarbageToken() {
        assertThat(jwtService.isValid("not-a-real-token")).isFalse();
        assertThat(jwtService.isValid("")).isFalse();
    }

    // Un token vencido no es válido.
    @Test
    void isValid_returnsFalseForExpiredToken() {
        // Expiración negativa: el token nace ya vencido (expira "antes" de crearse).
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1_000L);
        String expired = jwtService.generateToken("juan", UserEnum.USER);

        assertThat(jwtService.isValid(expired)).isFalse();
    }

    // Un token firmado con OTRA clave secreta no debe validar acá: así se
    // detecta un token falsificado o de otro servidor.
    @Test
    void isValid_returnsFalseWhenSignedWithDifferentSecret() {
        String token = jwtService.generateToken("juan", UserEnum.USER);

        // Otro JwtService con una clave distinta.
        JwtService other = new JwtService();
        ReflectionTestUtils.setField(other, "secret", "otra-clave-distinta-de-mas-de-32-caracteres-aaaa");
        ReflectionTestUtils.setField(other, "expirationMs", 3_600_000L);

        // El segundo no puede validar lo que firmó el primero.
        assertThat(other.isValid(token)).isFalse();
    }
}
