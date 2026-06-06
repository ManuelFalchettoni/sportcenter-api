package com.tpfinal.sportcenter_api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Le dice a Spring que este archivo es de configuración
@EnableWebSecurity // Activa la seguridad web en la aplicación
@EnableMethodSecurity // Permite bloquear métodos específicos más adelante (ej: @PreAuthorize)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authEntryPoint;

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

                // No guardamos sesión en el servidor; cada petición debe mandar su token
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Si alguien intenta entrar a un lugar prohibido, "authEntryPoint" maneja el error (ej: devuelve un 401)
                .exceptionHandling(e -> e.authenticationEntryPoint(authEntryPoint))

                // Acá configuramos los permisos de las rutas (URLs)
                .authorizeHttpRequests(auth -> auth
                        // Todo lo que sea /auth/ (login, registro) es público
                        .requestMatchers("/sportcenter/auth/**").permitAll()
                        // Crear un usuario (registro) mediante POST también es público
                        .requestMatchers(HttpMethod.POST, "/sportcenter/users").permitAll()
                        // Cualquier otra ruta no especificada arriba requiere estar logueado
                        .anyRequest().authenticated()
                )

                // Metemos nuestro filtro JWT justo antes del filtro de login tradicional
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Guardamos y aplicamos la configuración
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Configuramos el sistema que encripta las contraseñas en la base de datos (BCrypt)
        return new BCryptPasswordEncoder();
    }
}
