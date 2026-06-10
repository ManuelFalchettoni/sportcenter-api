package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotPendingException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test unitario con Mockito puro: sin Spring ni base de datos.
@ExtendWith(MockitoExtension.class)
class AppointmentConfirmServiceTest {

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;

    @Mock
    private AppointmentFinderService appointmentFinderService;

    @InjectMocks
    private AppointmentConfirmService service;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        // Grafo mínimo completo: toResponse navega user/professional/serviceType.
        User owner = new User(7L, "manu", "manu@example.com", "hash", UserEnum.USER,
                LocalDateTime.of(2026, 1, 1, 0, 0));
        Professional professional = new Professional("Juan Pérez", "Kinesiología", true);
        ServiceType serviceType = new ServiceType("Sesión de kinesiología", 45, new BigDecimal("8500.00"));

        appointment = new Appointment(
                LocalDateTime.of(2026, 7, 10, 10, 0),
                LocalDateTime.of(2026, 7, 10, 10, 45),
                null, owner, professional, serviceType);
        appointment.setId(5L);
        appointment.setCreatedAt(LocalDateTime.of(2026, 6, 1, 12, 0));
    }

    // PENDING -> CONFIRMED: cambia el estado, registra statusModifiedAt y persiste.
    @Test
    void confirm_setsStatusAndModifiedAtWhenPending() {
        appointment.setStatus(AppointmentStatusEnum.PENDING);
        when(appointmentFinderService.find(5L)).thenReturn(appointment);
        when(jpaAppointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentResponse response = service.confirm(5L);

        assertThat(response.getStatus()).isEqualTo(AppointmentStatusEnum.CONFIRMED);
        assertThat(response.getStatusModifiedAt()).isNotNull();
        verify(jpaAppointmentRepository).save(appointment);
    }

    // Confirmar dos veces -> 409 y no se persiste nada.
    @Test
    void confirm_throwsWhenAlreadyConfirmed() {
        appointment.setStatus(AppointmentStatusEnum.CONFIRMED);
        when(appointmentFinderService.find(5L)).thenReturn(appointment);

        assertThatThrownBy(() -> service.confirm(5L))
                .isInstanceOf(AppointmentNotPendingException.class)
                .hasMessageContaining("CONFIRMED");
        verify(jpaAppointmentRepository, never()).save(any(Appointment.class));
    }

    // Un turno cancelado no puede confirmarse: la cancelación es definitiva
    // (el horario pudo haberse vuelto a reservar).
    @Test
    void confirm_throwsWhenCancelled() {
        appointment.setStatus(AppointmentStatusEnum.CANCELLED);
        when(appointmentFinderService.find(5L)).thenReturn(appointment);

        assertThatThrownBy(() -> service.confirm(5L))
                .isInstanceOf(AppointmentNotPendingException.class)
                .hasMessageContaining("CANCELLED");
        verify(jpaAppointmentRepository, never()).save(any(Appointment.class));
    }

    // Id inexistente -> la NotFoundException del finder se propaga (404).
    @Test
    void confirm_propagatesNotFound() {
        when(appointmentFinderService.find(99L)).thenThrow(new AppointmentNotFoundException(99L));

        assertThatThrownBy(() -> service.confirm(99L))
                .isInstanceOf(AppointmentNotFoundException.class);
    }
}
