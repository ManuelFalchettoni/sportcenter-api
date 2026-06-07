package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.JwtService;
import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentCreatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentPostController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentCreatorService appointmentCreatorService;

    // JwtFilter (un Filter) se registra en el contexto de @WebMvcTest y exige JwtService.
    @MockitoBean
    private JwtService jwtService;

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusHours(1);

    // Construimos el JSON a mano para no depender de la config de Jackson para java.time.
    private String appointmentJson(LocalDateTime startTime, LocalDateTime endTime,
                                   String notes, Long userId, Long professionalId, Long serviceTypeId) {
        return """
                {"startTime":%s,"endTime":%s,"notes":%s,"userId":%s,"professionalId":%s,"serviceTypeId":%s}
                """.formatted(
                quote(startTime), quote(endTime), quote(notes),
                nullable(userId), nullable(professionalId), nullable(serviceTypeId));
    }

    private String quote(LocalDateTime value) {
        return value == null ? "null" : "\"" + value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"";
    }

    private String quote(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }

    private String nullable(Long value) {
        return value == null ? "null" : value.toString();
    }

    private String validJson() {
        return appointmentJson(start, end, "una nota", 1L, 2L, 3L);
    }

    private Appointment persistedAppointment() {
        User user = new User(1L, "juan", "juan@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        Professional professional = new Professional("Dr. House", "Clinica", true);
        professional.setId(2L);
        ServiceType serviceType = new ServiceType("Masaje", 60, new BigDecimal("100.00"));
        serviceType.setId(3L);
        Appointment appointment = new Appointment(start, end, "una nota", user, professional, serviceType);
        appointment.setId(10L);
        appointment.setConfirmed(false);
        appointment.setCreatedAt(LocalDateTime.now());
        return appointment;
    }

    @Test
    void create_returns201WithAppointmentResponse() throws Exception {
        when(appointmentCreatorService.create(any(AppointmentRequest.class)))
                .thenReturn(persistedAppointment());

        mockMvc.perform(post("/sportcenter/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.confirmed").value(false))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.professionalId").value(2))
                .andExpect(jsonPath("$.professionalName").value("Dr. House"))
                .andExpect(jsonPath("$.serviceTypeId").value(3))
                .andExpect(jsonPath("$.serviceTypeName").value("Masaje"));
    }

    @Test
    void create_returns404WhenProfessionalNotFound() throws Exception {
        when(appointmentCreatorService.create(any(AppointmentRequest.class)))
                .thenThrow(new ProfessionalNotFoundException(2L));

        mockMvc.perform(post("/sportcenter/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Professional with id: 2 not found."));
    }

    @Test
    void create_returns409WhenAppointmentsOverlap() throws Exception {
        when(appointmentCreatorService.create(any(AppointmentRequest.class)))
                .thenThrow(new AppointmentOverlapException(2L));

        mockMvc.perform(post("/sportcenter/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void create_returns400WhenServiceThrowsIllegalArgument() throws Exception {
        // El servicio valida endTime > startTime y lanza IllegalArgumentException -> 400.
        when(appointmentCreatorService.create(any(AppointmentRequest.class)))
                .thenThrow(new IllegalArgumentException("endTime must be after startTime"));

        mockMvc.perform(post("/sportcenter/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("endTime must be after startTime"));
    }

    @Test
    void create_returns400WhenRequiredFieldsAreMissing() throws Exception {
        // startTime null + userId null violan @NotNull antes de llegar al servicio.
        String invalid = appointmentJson(null, end, "nota", null, 2L, 3L);

        mockMvc.perform(post("/sportcenter/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.startTime").exists())
                .andExpect(jsonPath("$.errors.userId").exists());
    }

    @Test
    void create_returns400WhenDatesAreInThePast() throws Exception {
        // @Future: fechas pasadas deben rechazarse en la validación del DTO.
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        String invalid = appointmentJson(past, past.plusHours(1), "nota", 1L, 2L, 3L);

        mockMvc.perform(post("/sportcenter/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.startTime").exists());
    }
}
