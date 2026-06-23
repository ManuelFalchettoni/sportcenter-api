package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentFilterRequest;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentGetAllService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Misma receta que el resto: solo la capa web, filtros de seguridad apagados
// y la autenticación simulada en el SecurityContextHolder (ver AuthMeControllerTest).
@WebMvcTest(AppointmentGetAllController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentGetAllControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentGetAllService appointmentGetAllService;

    // Dependencias del JwtFilter, registrado en el contexto aunque los filtros estén apagados.
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void authenticate() {
        User caller = new User(7L, "manu", "manu@example.com", "hash", UserEnum.USER,
                LocalDateTime.of(2026, 1, 1, 0, 0));
        UserPrincipal principal = new UserPrincipal(caller);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // Todos los query params se parsean y llegan al servicio en el filtro.
    @Test
    void findAll_parsesAllFiltersIntoTheRequest() throws Exception {
        when(appointmentGetAllService.findAll(any(Pageable.class), any(User.class),
                any(AppointmentFilterRequest.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/sportcenter/appointments")
                        .param("from", "2026-07-01T00:00:00")
                        .param("to", "2026-07-31T23:59:59")
                        .param("status", "PENDING")
                        .param("professionalId", "2")
                        .param("query", "kinesio"))
                .andExpect(status().isOk());

        ArgumentCaptor<AppointmentFilterRequest> captor =
                ArgumentCaptor.forClass(AppointmentFilterRequest.class);
        verify(appointmentGetAllService).findAll(any(Pageable.class), any(User.class), captor.capture());
        AppointmentFilterRequest filter = captor.getValue();
        assertEquals(LocalDateTime.of(2026, 7, 1, 0, 0), filter.getFrom());
        assertEquals(LocalDateTime.of(2026, 7, 31, 23, 59, 59), filter.getTo());
        assertEquals(AppointmentStatusEnum.PENDING, filter.getStatus());
        assertEquals(2L, filter.getProfessionalId());
        assertEquals("kinesio", filter.getQuery());
    }

    // Sin query params -> 200 y filtro vacío: todos los filtros son opcionales.
    @Test
    void findAll_worksWithoutFilters() throws Exception {
        when(appointmentGetAllService.findAll(any(Pageable.class), any(User.class),
                any(AppointmentFilterRequest.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/sportcenter/appointments"))
                .andExpect(status().isOk());

        ArgumentCaptor<AppointmentFilterRequest> captor =
                ArgumentCaptor.forClass(AppointmentFilterRequest.class);
        verify(appointmentGetAllService).findAll(any(Pageable.class), any(User.class), captor.capture());
        assertNull(captor.getValue().getFrom());
        assertNull(captor.getValue().getTo());
        assertNull(captor.getValue().getStatus());
        assertNull(captor.getValue().getProfessionalId());
        assertNull(captor.getValue().getQuery());
    }

    // Un estado que no existe en el enum -> 400 (handler de type mismatch).
    @Test
    void findAll_returns400WhenStatusIsInvalid() throws Exception {
        mockMvc.perform(get("/sportcenter/appointments").param("status", "DONE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Una fecha mal formada -> 400 (la conversión a LocalDateTime falla antes del servicio).
    @Test
    void findAll_returns400WhenFromIsMalformed() throws Exception {
        mockMvc.perform(get("/sportcenter/appointments").param("from", "01/07/2026 10:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // from posterior a to -> el servicio lanza IllegalArgumentException y el
    // handler global responde 400 con el mensaje de la regla.
    @Test
    void findAll_returns400WhenFromIsAfterTo() throws Exception {
        when(appointmentGetAllService.findAll(any(Pageable.class), any(User.class),
                any(AppointmentFilterRequest.class)))
                .thenThrow(new IllegalArgumentException("'from' must be before or equal to 'to'"));

        mockMvc.perform(get("/sportcenter/appointments")
                        .param("from", "2026-07-31T00:00:00")
                        .param("to", "2026-07-01T00:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'from' must be before or equal to 'to'"));
    }
}
