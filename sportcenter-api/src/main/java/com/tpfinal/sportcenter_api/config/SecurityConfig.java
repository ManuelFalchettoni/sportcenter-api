package com.tpfinal.sportcenter_api.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Le dice a Spring que este archivo es de configuración
@EnableWebSecurity // Activa la seguridad web en la aplicación
@EnableMethodSecurity // Permite bloquear métodos específicos más adelante (ej: @PreAuthorize)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authEntryPoint;

    // Orígenes habilitados para CORS (frontend), separados por coma en properties.
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    // Conectamos nuestro filtro de tokens y el manejador de errores de acceso
    public SecurityConfig(JwtFilter jwtFilter, JwtAuthenticationEntryPoint authEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF porque con tokens JWT ya estamos protegidos de ese ataque
                .csrf(csrf -> csrf.disable())

                // Habilitamos CORS con el bean corsConfigurationSource de abajo,
                // para que el navegador permita las llamadas desde el frontend
                .cors(Customizer.withDefaults())

                // No guardamos sesión en el servidor; cada petición debe mandar su token
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Si alguien intenta entrar a un lugar prohibido, "authEntryPoint" maneja el error (ej: devuelve un 401)
                .exceptionHandling(e -> e.authenticationEntryPoint(authEntryPoint))

                // Acá configuramos los permisos de las rutas (URLs)
                .authorizeHttpRequests(auth -> auth
                        // /auth/me devuelve el usuario del token: requiere estar logueado.
                        // Va ANTES del permitAll de /auth/** porque gana la primera regla que matchea.
                        .requestMatchers("/sportcenter/auth/me").authenticated()
                        // Todo lo que sea /auth/ (login, registro) es público
                        .requestMatchers("/sportcenter/auth/**").permitAll()
                        // Crear un usuario (registro) mediante POST también es público
                        .requestMatchers(HttpMethod.POST, "/sportcenter/users").permitAll()
                        // Documentación OpenAPI: la spec JSON y Swagger UI son públicas
                        // (los endpoints protegidos igual exigen token al ejecutarlos)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Cualquier otra ruta no especificada arriba requiere estar logueado
                        .anyRequest().authenticated()
                )

                // Metemos nuestro filtro JWT justo antes del filtro de login tradicional
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Guardamos y aplicamos la configuración
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Solo los orígenes declarados en properties pueden llamar a la API desde un navegador.
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Location viaja en los 201 Created; sin exponerlo el JS del navegador no puede leerlo.
        config.setExposedHeaders(List.of("Location"));
        // No usamos cookies: el JWT viaja en el header Authorization, así que
        // no hace falta allowCredentials (y dejarlo apagado es más seguro).

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Configuramos el sistema que encripta las contraseñas en la base de datos (BCrypt)
        return new BCryptPasswordEncoder();
    }
}
