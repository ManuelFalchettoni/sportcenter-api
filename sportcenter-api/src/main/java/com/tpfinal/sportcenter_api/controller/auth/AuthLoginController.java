package com.tpfinal.sportcenter_api.controller.auth;

import com.tpfinal.sportcenter_api.dto.request.auth.LoginRequest;
import com.tpfinal.sportcenter_api.dto.response.auth.LoginResponse;
import com.tpfinal.sportcenter_api.service.auth.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint público de login. Ruta base: {@code /sportcenter/auth}.
 * Declarado público en {@link com.tpfinal.sportcenter_api.config.SecurityConfig}.
 */
@RestController
@RequestMapping("/sportcenter/auth")
public class AuthLoginController {

    private final LoginService loginService;

    public AuthLoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }
}
