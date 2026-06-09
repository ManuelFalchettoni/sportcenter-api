package com.tpfinal.sportcenter_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que extrae el JWT del header  Authorization: Bearer ...,
 * lo valida y, si es válido, carga el usuario real desde la base
 * (UserDetailsService) y deposita la Authentication en el
 * SecurityContextHolder para que el resto de Spring Security.
 * El token solo prueba identidad: el rol y la existencia del usuario
 * se leen de la DB en cada request. Así un usuario borrado o
 * con rol cambiado no sigue operando con un token viejo.
 * responde JwtAuthenticationEntryPoint.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Conectamos el servicio que valida tokens y el que carga usuarios de la DB
    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
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
                try {
                    // 4. El token prueba quién es; los datos reales (rol incluido)
                    //    salen de la base, no de los claims
                    String username = jwtService.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // credentials = null porque ya autenticamos vía token, no hay password
                    var auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // Le decimos al sistema de seguridad: "Este usuario es válido, dale acceso"
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (UsernameNotFoundException e) {
                    // Token firmado y vigente pero el usuario ya no existe en la DB
                    // (fue borrado): no autenticamos y la cadena devolverá 401.
                }
            }
        }

        // 5. Pase lo que pase, dejamos que la petición siga su camino al siguiente control
        filterChain.doFilter(request, response);
    }
}
