package com.tpfinal.sportcenter_api.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException;
import com.tpfinal.sportcenter_api.service.user.UserCreatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test de capa web del controller de alta de usuarios (solo la capa web).
@WebMvcTest(UserPostController.class)
@AutoConfigureMockMvc(addFilters = false) // sin filtros de seguridad, para enfocarnos en el endpoint
class UserPostControllerTest {

    @Autowired
    private MockMvc mockMvc; // simula las peticiones HTTP

    private final ObjectMapper objectMapper = new ObjectMapper(); // objeto <-> JSON

    @MockitoBean
    private UserCreatorService userCreatorService; // servicio mockeado

    // JwtFilter (un Filter) se registra en el contexto de @WebMvcTest y exige
    // JwtService y UserDetailsService.
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    // Alta OK -> 201 con el usuario en el cuerpo (y SIN exponer la contraseña).
    @Test
    void create_returns201WithUserResponse() throws Exception {
        // El servicio devuelve un usuario ya guardado (con id y hash).
        User saved = new User(1L, "juan", "juan@mail.com", "$2a$10$hash", UserEnum.USER, LocalDateTime.now());
        when(userCreatorService.create(any(UserRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan", "juan@mail.com", "secret123"))))
                .andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                // El response NUNCA debe exponer la contraseña (ni el hash).
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    // Usuario duplicado -> el servicio lanza la excepción -> 409.
    @Test
    void create_returns409WhenUserAlreadyExists() throws Exception {
        when(userCreatorService.create(any(UserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("email", "juan@mail.com"));

        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan", "juan@mail.com", "secret123"))))
                .andExpect(status().isConflict()) // 409
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email 'juan@mail.com' already exists."));
    }

    // De acá en adelante: validaciones del DTO que fallan ANTES de llegar al
    // servicio y devuelven 400 con el campo problemático bajo "errors".

    // Username muy corto (mínimo de caracteres).
    @Test
    void create_returns400WhenUsernameTooShort() throws Exception {
        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("ab", "juan@mail.com", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    // Username con caracteres no permitidos (espacio y "!").
    @Test
    void create_returns400WhenUsernameHasIllegalCharacters() throws Exception {
        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan perez!", "juan@mail.com", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    // Email mal formado.
    @Test
    void create_returns400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan", "not-an-email", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    // Contraseña muy corta.
    @Test
    void create_returns400WhenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan", "juan@mail.com", "short"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
