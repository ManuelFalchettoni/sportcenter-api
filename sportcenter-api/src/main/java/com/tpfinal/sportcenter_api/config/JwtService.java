package com.tpfinal.sportcenter_api.config;

import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Emisión y verificación de tokens JWT (HS256). API jjwt 0.12.6.
 *
 * <p>Subject = username. Claim "role" = UserEnum como string.
 * Las authorities se exponen como ROLE_<rol> para que
 * hasRole('ADMIN') matchee.
 */
@Service
public class JwtService {

    // Secret en texto plano desde properties. HS256 exige ≥ 32 bytes (256 bits).
    @Value("${jwt.secret}")
    private String secret;

    // Vida del token en milisegundos.
    @Value("${jwt.expiration}")
    private Long expirationMs;

    // Clave derivada del secret. Se construye una vez por request (barato).
    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Emite un token firmado con el username como subject y el rol como claim. */
    public String generateToken(String username, UserEnum role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    /** True si la firma es válida y el token no expiró. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Firma inválida, expirado, malformado, null o vacío.
            return false;
        }
    }

    /**
     * Construye el Authentication que va al SecurityContext.
     * Asume que isValid(String) ya pasó: si el token es inválido tira excepción.
     */
    public Authentication toAuthentication(String token) {
        Claims claims = parse(token);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        // Prefijo ROLE_ obligatorio para que hasRole('ADMIN') funcione.
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        // credentials = null porque ya autenticamos vía token, no hay password.
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    // Verifica firma + expiración y devuelve los claims.
    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
