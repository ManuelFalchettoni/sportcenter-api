package com.tpfinal.sportcenter_api.config;

import org.springframework.security.core.Authentication;

/**
 * Contrato que el filtro {@link JwtFilter} espera de la capa de tokens.
 * <p>
 * La implementación concreta (firma, claims, expiración, biblioteca usada)
 * queda fuera de este paquete. Sugerido: {@code io.jsonwebtoken:jjwt} o
 * {@code com.auth0:java-jwt}, registrando una clase con {@code @Service}
 * que implemente esta interfaz.
 *
 * <p>Recordá que al construir el {@link Authentication}:
 * <ul>
 *   <li>el principal suele ser el username (o un {@code UserDetails}),</li>
 *   <li>las authorities deben venir prefijadas con {@code ROLE_} para que
 *       {@code hasRole('ADMIN')} matchee (ej.: {@code ROLE_ADMIN}).</li>
 * </ul>
 */
public interface JwtService {

    boolean isValid(String token);

    Authentication toAuthentication(String token);
}
