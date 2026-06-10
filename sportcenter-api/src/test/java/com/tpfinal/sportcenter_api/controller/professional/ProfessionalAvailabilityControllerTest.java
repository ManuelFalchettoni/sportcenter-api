package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalAvailabilityResponse;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalAvailabilityResponse.BusySlot;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalAvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Misma receta que el resto de los controller tests: solo la capa web,
// filtros de seguridad apagados y el servicio mockeado.
@WebMvcTest(ProfessionalAvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfessionalAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionalAvailabilityService professionalAvailabilityService;

    // Dependencias del JwtFilter, registrado en el contexto aunque los filtros estén apagados.
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    // Día con turnos -> 200 con los rangos ocupados (y solo los rangos: nada
    // de usuarios, notas ni ids de turnos).
    @Test
    void availability_returns200WithBusySlots() throws Exception {
        LocalDate date = LocalDate.of(2026, 7, 10);
        when(professionalAvailabilityService.findBusySlots(2L, date))
                .thenReturn(new ProfessionalAvailabilityResponse(2L, date, List.of(
                        new BusySlot(LocalDateTime.of(2026, 7, 10, 10, 0),
                                LocalDateTime.of(2026, 7, 10, 10, 45)))));

        mockMvc.perform(get("/sportcenter/professionals/2/availability").param("date", "2026-07-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.professionalId").value(2))
                .andExpect(jsonPath("$.date").value("2026-07-10"))
                .andExpect(jsonPath("$.busySlots[0].startTime").value("2026-07-10T10:00:00"))
                .andExpect(jsonPath("$.busySlots[0].endTime").value("2026-07-10T10:45:00"))
                // Ningún dato del turno ni de quién lo reservó debe filtrarse.
                .andExpect(jsonPath("$.busySlots[0].username").doesNotExist())
                .andExpect(jsonPath("$.busySlots[0].userId").doesNotExist())
                .andExpect(jsonPath("$.busySlots[0].notes").doesNotExist())
                .andExpect(jsonPath("$.busySlots[0].id").doesNotExist());
    }

    // Profesional inexistente -> 404 con el body uniforme del handler global.
    @Test
    void availability_returns404WhenProfessionalDoesNotExist() throws Exception {
        when(professionalAvailabilityService.findBusySlots(eq(99L), any(LocalDate.class)))
                .thenThrow(new ProfessionalNotFoundException(99L));

        mockMvc.perform(get("/sportcenter/professionals/99/availability").param("date", "2026-07-10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // Sin date -> 400: el parámetro es obligatorio.
    @Test
    void availability_returns400WhenDateIsMissing() throws Exception {
        mockMvc.perform(get("/sportcenter/professionals/2/availability"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // date mal formado -> 400 (la conversión a LocalDate falla antes del servicio).
    @Test
    void availability_returns400WhenDateIsMalformed() throws Exception {
        mockMvc.perform(get("/sportcenter/professionals/2/availability").param("date", "10/07/2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
