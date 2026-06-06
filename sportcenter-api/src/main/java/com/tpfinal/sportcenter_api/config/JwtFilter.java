package com.tpfinal.sportcenter_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que extrae el JWT del header {@code Authorization: Bearer ...},
 * lo valida y, si es válido, deposita la {@link Authentication} en el
 * {@link SecurityContextHolder} para que el resto de Spring Security
 * (autorización, {@code @PreAuthorize}, {@code @AuthenticationPrincipal})
 * pueda operar.
 *
 * <p>No rechaza requests sin token ni con token inválido: esa decisión la
 * toma {@code SecurityFilterChain} según las reglas de autorización
 * configuradas. Si no hay autenticación cuando se exige, responde
 * {@link JwtAuthenticationEntryPoint}.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isValid(token)) {
                Authentication auth = jwtService.toAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
