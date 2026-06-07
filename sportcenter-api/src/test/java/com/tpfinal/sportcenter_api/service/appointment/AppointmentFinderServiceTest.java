package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentFinderServiceTest {

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;

    @InjectMocks
    private AppointmentFinderService service;

    @Test
    void find_returnsAppointmentWhenExists() {
        Appointment appointment = new Appointment();
        appointment.setId(5L);
        when(jpaAppointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));

        assertThat(service.find(5L)).isSameAs(appointment);
    }

    @Test
    void find_throwsWhenNotFound() {
        when(jpaAppointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.find(99L))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");
    }
}
