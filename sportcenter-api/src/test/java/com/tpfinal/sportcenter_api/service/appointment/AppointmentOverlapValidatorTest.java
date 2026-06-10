package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.appointment.UserAppointmentOverlapException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// Activa Mockito: procesa los @Mock y el @InjectMocks antes de cada test.
@ExtendWith(MockitoExtension.class)
class AppointmentOverlapValidatorTest {

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;

    @InjectMocks
    private AppointmentOverlapValidator validator;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = start.plusHours(1);
    }

    // Caso feliz al crear: ni el profesional ni el usuario tienen turnos
    // solapados (los mocks devuelven false por defecto) -> no tira nada.
    @Test
    void checkForCreate_passesWhenNoOverlap() {
        assertThatCode(() -> validator.checkForCreate(2L, 1L, start, end))
                .doesNotThrowAnyException();
    }

    // El profesional ya tiene un turno activo que pisa el rango.
    @Test
    void checkForCreate_throwsWhenProfessionalOverlaps() {
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
                2L, end, start, AppointmentStatusEnum.CANCELLED)).thenReturn(true);

        assertThatThrownBy(() -> validator.checkForCreate(2L, 1L, start, end))
                .isInstanceOf(AppointmentOverlapException.class);
    }

    // El horario del profesional está libre, pero el usuario ya tiene otro
    // turno (con cualquier profesional) que pisa el rango.
    @Test
    void checkForCreate_throwsWhenUserOverlaps() {
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
                2L, end, start, AppointmentStatusEnum.CANCELLED)).thenReturn(false);
        when(jpaAppointmentRepository.existsByUserIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
                1L, end, start, AppointmentStatusEnum.CANCELLED)).thenReturn(true);

        assertThatThrownBy(() -> validator.checkForCreate(2L, 1L, start, end))
                .isInstanceOf(UserAppointmentOverlapException.class);
    }

    // Caso feliz al actualizar: las variantes IdNot excluyen al propio turno,
    // así que editarlo sin moverlo (o solo cambiar las notas) no choca.
    @Test
    void checkForUpdate_passesWhenNoOverlap() {
        assertThatCode(() -> validator.checkForUpdate(2L, 1L, start, end, 10L))
                .doesNotThrowAnyException();
    }

    // El nuevo rango pisa OTRO turno del profesional (distinto del que se edita).
    @Test
    void checkForUpdate_throwsWhenProfessionalOverlaps() {
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndStatusNot(
                2L, end, start, 10L, AppointmentStatusEnum.CANCELLED)).thenReturn(true);

        assertThatThrownBy(() -> validator.checkForUpdate(2L, 1L, start, end, 10L))
                .isInstanceOf(AppointmentOverlapException.class);
    }

    // El nuevo rango pisa OTRO turno del dueño (distinto del que se edita).
    @Test
    void checkForUpdate_throwsWhenUserOverlaps() {
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndStatusNot(
                2L, end, start, 10L, AppointmentStatusEnum.CANCELLED)).thenReturn(false);
        when(jpaAppointmentRepository.existsByUserIdAndStartTimeBeforeAndEndTimeAfterAndIdNotAndStatusNot(
                1L, end, start, 10L, AppointmentStatusEnum.CANCELLED)).thenReturn(true);

        assertThatThrownBy(() -> validator.checkForUpdate(2L, 1L, start, end, 10L))
                .isInstanceOf(UserAppointmentOverlapException.class);
    }
}
