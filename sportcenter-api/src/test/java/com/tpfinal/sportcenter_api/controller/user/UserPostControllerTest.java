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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserPostController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserCreatorService userCreatorService;

    // JwtFilter (un Filter) se registra en el contexto de @WebMvcTest y exige JwtService.
    @MockitoBean
    private JwtService jwtService;

    @Test
    void create_returns201WithUserResponse() throws Exception {
        User saved = new User(1L, "juan", "juan@mail.com", "$2a$10$hash", UserEnum.USER, LocalDateTime.now());
        when(userCreatorService.create(any(UserRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan", "juan@mail.com", "secret123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                // El response NUNCA debe exponer la contraseña (ni el hash).
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void create_returns409WhenUserAlreadyExists() throws Exception {
        when(userCreatorService.create(any(UserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("email", "juan@mail.com"));

        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan", "juan@mail.com", "secret123"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email 'juan@mail.com' already exists."));
    }

    @Test
    void create_returns400WhenUsernameTooShort() throws Exception {
        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("ab", "juan@mail.com", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    @Test
    void create_returns400WhenUsernameHasIllegalCharacters() throws Exception {
        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan perez!", "juan@mail.com", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    @Test
    void create_returns400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/sportcenter/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("juan", "not-an-email", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

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
