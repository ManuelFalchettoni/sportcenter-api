package com.tpfinal.sportcenter_api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpfinal.sportcenter_api.dto.request.auth.LoginRequest;
import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.response.auth.LoginResponse;
import com.tpfinal.sportcenter_api.exception.auth.InvalidCredentialsException;
import com.tpfinal.sportcenter_api.service.auth.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// addFilters = false: aislamos la capa web de la cadena de filtros de seguridad.
// El login es público de todos modos, y aquí solo nos interesan status, validación
// y el formato de error del GlobalExceptionHandler.
@WebMvcTest(AuthLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private LoginService loginService;

    // JwtFilter es un Filter y se registra en el contexto de @WebMvcTest, así que
    // hay que satisfacer su dependencia aunque los filtros estén desactivados.
    @MockitoBean
    private JwtService jwtService;

    @Test
    void login_returns200WithToken() throws Exception {
        when(loginService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("signed.jwt.token"));

        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("juan@mail.com", "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("signed.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_returns401OnInvalidCredentials() throws Exception {
        when(loginService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("juan@mail.com", "wrongpass"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test
    void login_returns400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("not-an-email", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void login_returns400WhenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("juan@mail.com", "short"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
