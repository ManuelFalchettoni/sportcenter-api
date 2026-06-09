package com.tpfinal.sportcenter_api.config;

import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Emisión y verificación de tokens JWT (HS256). API jjwt 0.12.6.
 *
 * <p>Subject = username. El claim "role" es informativo para el cliente:
 * el servidor autoriza con el rol cargado desde la DB
 * (CustomUserDetailsService), no con el del token.
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
     * Devuelve el username (subject) del token.
     * Asume que isValid(String) ya pasó: si el token es inválido tira excepción.
     */
    public String extractUsername(String token) {
        return parse(token).getSubject();
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
