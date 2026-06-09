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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest levanta SOLO la capa web del controller indicado (no la app entera
// ni la base de datos): controller + validación + manejo de errores + Jackson.
// addFilters = false: aislamos la capa web de la cadena de filtros de seguridad.
// El login es público de todos modos, y aquí solo nos interesan status, validación
// y el formato de error del GlobalExceptionHandler.
@WebMvcTest(AuthLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthLoginControllerTest {

    // MockMvc simula peticiones HTTP al controller sin levantar un servidor real.
    @Autowired
    private MockMvc mockMvc;

    // Jackson: convierte objetos Java <-> JSON. Lo usamos para armar el body.
    private final ObjectMapper objectMapper = new ObjectMapper();

    // @MockitoBean reemplaza el bean real por un mock dentro del contexto de Spring.
    // Así el controller usa este LoginService falso en vez del verdadero.
    @MockitoBean
    private LoginService loginService;

    // JwtFilter es un Filter y se registra en el contexto de @WebMvcTest, así que
    // hay que satisfacer sus dependencias aunque los filtros estén desactivados.
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    // Login OK -> 200 con el token en el cuerpo.
    @Test
    void login_returns200WithToken() throws Exception {
        // El servicio (mockeado) devuelve un token cualquiera.
        when(loginService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("signed.jwt.token"));

        // perform: ejecuta un POST con cuerpo JSON. andExpect: verifica la respuesta.
        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON) // header Content-Type
                        .content(objectMapper.writeValueAsString(new LoginRequest("juan@mail.com", "secret123"))))
                .andExpect(status().isOk()) // 200
                // jsonPath navega el JSON de respuesta: $ = raíz, .token = campo.
                .andExpect(jsonPath("$.token").value("signed.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    // Credenciales inválidas -> el servicio lanza la excepción y el handler la
    // traduce a 401 con el body uniforme.
    @Test
    void login_returns401OnInvalidCredentials() throws Exception {
        when(loginService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException()); // thenThrow: simula el error

        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("juan@mail.com", "wrongpass"))))
                .andExpect(status().isUnauthorized()) // 401
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    // Email mal formado -> falla la validación @Email del DTO ANTES del servicio
    // -> 400 con el detalle del campo bajo "errors".
    @Test
    void login_returns400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("not-an-email", "secret123"))))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errors.email").exists()); // hay un error en el campo email
    }

    // Contraseña demasiado corta -> falla la validación de tamaño -> 400.
    @Test
    void login_returns400WhenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/sportcenter/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("juan@mail.com", "short"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
