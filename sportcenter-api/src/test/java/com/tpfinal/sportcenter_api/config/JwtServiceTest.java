package com.tpfinal.sportcenter_api.config;

import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // HS256 exige una clave de al menos 32 bytes.
        ReflectionTestUtils.setField(jwtService, "secret", "clave-de-prueba-con-mas-de-32-caracteres-1234567890");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3_600_000L);
    }

    @Test
    void generateToken_producesValidToken() {
        String token = jwtService.generateToken("juan", UserEnum.USER);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void toAuthentication_exposesUsernameAndRoleAuthority() {
        String token = jwtService.generateToken("juan", UserEnum.ADMIN);

        Authentication auth = jwtService.toAuthentication(token);

        assertThat(auth.getPrincipal()).isEqualTo("juan");
        assertThat(auth.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }

    @Test
    void isValid_returnsFalseForGarbageToken() {
        assertThat(jwtService.isValid("not-a-real-token")).isFalse();
        assertThat(jwtService.isValid("")).isFalse();
    }

    @Test
    void isValid_returnsFalseForExpiredToken() {
        // Expiración negativa: el token nace ya vencido.
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1_000L);
        String expired = jwtService.generateToken("juan", UserEnum.USER);

        assertThat(jwtService.isValid(expired)).isFalse();
    }

    @Test
    void isValid_returnsFalseWhenSignedWithDifferentSecret() {
        String token = jwtService.generateToken("juan", UserEnum.USER);

        JwtService other = new JwtService();
        ReflectionTestUtils.setField(other, "secret", "otra-clave-distinta-de-mas-de-32-caracteres-aaaa");
        ReflectionTestUtils.setField(other, "expirationMs", 3_600_000L);

        assertThat(other.isValid(token)).isFalse();
    }
}
