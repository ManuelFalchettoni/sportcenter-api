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
 * Filtro que extrae el JWT del header  Authorization: Bearer ...,
 * lo valida y, si es válido, deposita la Authentication en el
 * SecurityContextHolder para que el resto de Spring Security
 * (autorización, @PreAuthorize, @AuthenticationPrincipal)
 * pueda operar.
 *
 * <p>No rechaza requests sin token ni con token inválido: esa decisión la
 * toma SecurityFilterChain según las reglas de autorización
 * configuradas. Si no hay autenticación cuando se exige, responde
 * JwtAuthenticationEntryPoint.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // Conectamos el servicio que sabe cómo validar y leer los tokens
    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // Este metodo se ejecuta automáticamente con cada petición que llega
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Buscamos la etiqueta "Authorization" en los datos que envió el usuario
        String authHeader = request.getHeader("Authorization");

        // 2. Si existe esa etiqueta y empieza con "Bearer ", encontramos un token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Recortamos la palabra "Bearer " para quedarnos solo con el código del token
            String token = authHeader.substring(7);

            // 3. Le preguntamos al servicio si el token es real y vigente
            if (jwtService.isValid(token)) {

                // Extraemos la información del usuario que viene dentro del token
                Authentication auth = jwtService.toAuthentication(token);

                // Le decimos al sistema de seguridad: "Este usuario es válido, dale acceso"
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 4. Pase lo que pase, dejamos que la petición siga su camino al siguiente control
        filterChain.doFilter(request, response);
    }
}