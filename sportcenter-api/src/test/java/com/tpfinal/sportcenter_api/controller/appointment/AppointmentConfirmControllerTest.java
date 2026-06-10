package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotPendingException;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentConfirmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Misma receta que el resto: solo la capa web, sin filtros de seguridad.
// La regla solo-ADMIN vive en el @PreAuthorize (method security) y acá no se
// ejercita; lo que se prueba es el routing y la traducción de errores.
@WebMvcTest(AppointmentConfirmController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentConfirmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentConfirmService appointmentConfirmService;

    // Dependencias del JwtFilter, registrado en el contexto aunque los filtros estén apagados.
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    // Turno PENDING -> 200 con el estado CONFIRMED y statusModifiedAt seteado.
    @Test
    void confirm_returns200WithConfirmedAppointment() throws Exception {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(5L);
        response.setStatus(AppointmentStatusEnum.CONFIRMED);
        response.setStatusModifiedAt(LocalDateTime.of(2026, 6, 10, 12, 0));
        when(appointmentConfirmService.confirm(5L)).thenReturn(response);

        mockMvc.perform(patch("/sportcenter/appointments/5/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.statusModifiedAt").exists());
    }

    // Turno que no está PENDING (ya confirmado o cancelado) -> 409.
    @Test
    void confirm_returns409WhenNotPending() throws Exception {
        when(appointmentConfirmService.confirm(5L))
                .thenThrow(new AppointmentNotPendingException(5L, AppointmentStatusEnum.CANCELLED));

        mockMvc.perform(patch("/sportcenter/appointments/5/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // Id inexistente -> 404 con el body uniforme del handler global.
    @Test
    void confirm_returns404WhenAppointmentDoesNotExist() throws Exception {
        when(appointmentConfirmService.confirm(99L))
                .thenThrow(new AppointmentNotFoundException(99L));

        mockMvc.perform(patch("/sportcenter/appointments/99/confirm"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
