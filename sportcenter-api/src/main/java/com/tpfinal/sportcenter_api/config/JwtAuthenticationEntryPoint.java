package com.tpfinal.sportcenter_api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Responde 401 en JSON con el mismo shape que usa GlobalExceptionHandler.
 * Construye el body a mano para no depender de Jackson (no viene en webmvc starter).
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Captura intentos de acceso de usuarios no autenticados y devuelve JSON.
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Escapamos el mensaje para no romper el JSON si trae comillas o backslashes.
        String message = escape(authException.getMessage());

        String body = "{"
                + "\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + HttpStatus.UNAUTHORIZED.value() + ","
                + "\"error\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\","
                + "\"message\":\"" + message + "\""
                + "}";

        response.getWriter().write(body);
    }
    //formateador de seguridad que limpia los texto para que puedan viajar o
    // guardarse en otros formatos sin romper la estructura del sistema.
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
