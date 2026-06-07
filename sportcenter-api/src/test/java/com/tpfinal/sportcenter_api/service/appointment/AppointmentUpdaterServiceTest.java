package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.user.UserNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentUpdaterServiceTest {

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;
    @Mock
    private JpaUserRepository jpaUserRepository;
    @Mock
    private JpaProfessionalRepository jpaProfessionalRepository;
    @Mock
    private JpaServiceTypeRepository jpaServiceTypeRepository;
    @Mock
    private AppointmentFinderService appointmentFinderService;

    @InjectMocks
    private AppointmentUpdaterService service;

    private LocalDateTime start;
    private LocalDateTime end;
    private User user;
    private Professional professional;
    private ServiceType serviceType;
    private Appointment existing;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = start.plusHours(1);
        user = new User(1L, "juan", "juan@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        professional = new Professional("Dr. House", "Clinica", true);
        professional.setId(2L);
        serviceType = new ServiceType("Masaje", 60, new BigDecimal("100.00"));
        serviceType.setId(3L);
        existing = new Appointment(start, end, "vieja nota", user, professional, serviceType);
        existing.setId(10L);
        existing.setConfirmed(false);
        existing.setCreatedAt(LocalDateTime.now().minusDays(2));
    }

    private AppointmentRequest request() {
        return new AppointmentRequest(start, end, "nueva nota", 1L, 2L, 3L);
    }

    @Test
    void update_appliesChangesAndReturnsResponse() {
        when(appointmentFinderService.find(10L)).thenReturn(existing);
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.of(serviceType));
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNot(
                2L, end, start, 10L)).thenReturn(false);
        when(jpaAppointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = service.update(10L, request());

        assertThat(existing.getNotes()).isEqualTo("nueva nota");
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getNotes()).isEqualTo("nueva nota");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getProfessionalId()).isEqualTo(2L);
        assertThat(response.getServiceTypeId()).isEqualTo(3L);
    }

    @Test
    void update_rejectsRangeWhenEndIsNotAfterStart() {
        AppointmentRequest req = new AppointmentRequest(end, start, "nota", 1L, 2L, 3L);

        assertThatThrownBy(() -> service.update(10L, req))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    @Test
    void update_throwsWhenUserNotFound() {
        when(appointmentFinderService.find(10L)).thenReturn(existing);
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(10L, request()))
                .isInstanceOf(UserNotFoundException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    @Test
    void update_throwsWhenOverlappingWithAnotherAppointment() {
        when(appointmentFinderService.find(10L)).thenReturn(existing);
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.of(serviceType));
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndIdNot(
                2L, end, start, 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(10L, request()))
                .isInstanceOf(AppointmentOverlapException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }
}
