package com.tpfinal.sportcenter_api.controller.auth;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Misma receta que AuthLoginControllerTest: solo la capa web, sin filtros reales.
// Como los filtros están apagados, la autenticación se simula poniendo el
// UserPrincipal directo en el SecurityContextHolder (lo que haría el JwtFilter);
// @AuthenticationPrincipal lo resuelve desde ahí.
@WebMvcTest(AuthMeController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthMeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Dependencias del JwtFilter, registrado en el contexto aunque los filtros estén apagados.
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    // Limpia el contexto para no contaminar otros tests del mismo hilo.
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // Usuario autenticado -> 200 con sus datos (y nunca el password).
    @Test
    void me_returns200WithCurrentUser() throws Exception {
        User authenticated = new User(7L, "manu", "manu@example.com",
                "$2a$10$hashed-password-never-exposed", UserEnum.USER,
                LocalDateTime.of(2026, 1, 15, 10, 0));
        UserPrincipal principal = new UserPrincipal(authenticated);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(get("/sportcenter/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.username").value("manu"))
                .andExpect(jsonPath("$.email").value("manu@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}
