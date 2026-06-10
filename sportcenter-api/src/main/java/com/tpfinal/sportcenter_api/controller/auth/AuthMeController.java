package com.tpfinal.sportcenter_api.controller.auth;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Devuelve el usuario autenticado actual (el dueño del token).
 * Ruta: GET /sportcenter/auth/me.
 *
 * El frontend lo necesita tras el login: el token no lleva el id ni el email,
 * y sin el id no puede llamar a GET /users/{id} ni saber qué mostrar según rol.
 * A diferencia del resto de /auth/**, esta ruta requiere token (ver SecurityConfig).
 */
@RestController
@RequestMapping("/sportcenter/auth")
public class AuthMeController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        // El JwtFilter ya cargó el usuario real desde la DB en cada request:
        // alcanza con mapearlo al DTO de respuesta (sin password).
        return ResponseEntity.ok(UserResponse.toResponse(principal.getUser()));
    }
}
