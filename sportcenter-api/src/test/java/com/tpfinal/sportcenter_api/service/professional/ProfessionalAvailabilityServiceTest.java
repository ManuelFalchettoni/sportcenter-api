package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalAvailabilityResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test unitario con Mockito puro: sin Spring ni base de datos.
@ExtendWith(MockitoExtension.class)
class ProfessionalAvailabilityServiceTest {

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;

    @Mock
    private ProfessionalFinderService professionalFinderService;

    @InjectMocks
    private ProfessionalAvailabilityService service;

    private static final Long PROFESSIONAL_ID = 2L;
    private static final LocalDate DATE = LocalDate.of(2026, 7, 10);

    // Día con turnos -> devuelve los rangos ocupados, y nada más que los rangos.
    @Test
    void findBusySlots_returnsBusyRangesOfTheDay() {
        Appointment morning = new Appointment(
                LocalDateTime.of(2026, 7, 10, 10, 0), LocalDateTime.of(2026, 7, 10, 10, 45),
                null, null, null, null);
        Appointment afternoon = new Appointment(
                LocalDateTime.of(2026, 7, 10, 15, 0), LocalDateTime.of(2026, 7, 10, 16, 0),
                null, null, null, null);

        when(jpaAppointmentRepository
                .findByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNotOrderByStartTimeAsc(
                        PROFESSIONAL_ID,
                        DATE.plusDays(1).atStartOfDay(), // fin de la ventana (exclusivo)
                        DATE.atStartOfDay(),             // inicio de la ventana
                        AppointmentStatusEnum.CANCELLED))
                .thenReturn(List.of(morning, afternoon));

        ProfessionalAvailabilityResponse response = service.findBusySlots(PROFESSIONAL_ID, DATE);

        assertEquals(PROFESSIONAL_ID, response.getProfessionalId());
        assertEquals(DATE, response.getDate());
        assertEquals(2, response.getBusySlots().size());
        assertEquals(LocalDateTime.of(2026, 7, 10, 10, 0), response.getBusySlots().get(0).getStartTime());
        assertEquals(LocalDateTime.of(2026, 7, 10, 10, 45), response.getBusySlots().get(0).getEndTime());
        assertEquals(LocalDateTime.of(2026, 7, 10, 15, 0), response.getBusySlots().get(1).getStartTime());
        assertEquals(LocalDateTime.of(2026, 7, 10, 16, 0), response.getBusySlots().get(1).getEndTime());
    }

    // Día sin turnos -> lista vacía (no null): el día está completamente libre.
    @Test
    void findBusySlots_returnsEmptyListWhenDayIsFree() {
        when(jpaAppointmentRepository
                .findByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNotOrderByStartTimeAsc(
                        any(), any(), any(), any()))
                .thenReturn(List.of());

        ProfessionalAvailabilityResponse response = service.findBusySlots(PROFESSIONAL_ID, DATE);

        assertTrue(response.getBusySlots().isEmpty());
    }

    // Profesional inexistente -> 404 antes de consultar la agenda: un id
    // inválido no debe confundirse con un día libre.
    @Test
    void findBusySlots_throwsNotFoundWhenProfessionalDoesNotExist() {
        when(professionalFinderService.find(PROFESSIONAL_ID))
                .thenThrow(new ProfessionalNotFoundException(PROFESSIONAL_ID));

        assertThrows(ProfessionalNotFoundException.class,
                () -> service.findBusySlots(PROFESSIONAL_ID, DATE));

        verify(jpaAppointmentRepository, never())
                .findByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNotOrderByStartTimeAsc(
                        anyLong(), any(), any(), any());
    }
}
