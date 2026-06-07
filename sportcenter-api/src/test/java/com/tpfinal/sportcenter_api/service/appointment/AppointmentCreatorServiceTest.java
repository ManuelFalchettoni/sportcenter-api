package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentRequest;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.exception.user.UserNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class AppointmentCreatorServiceTest {

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;
    @Mock
    private JpaUserRepository jpaUserRepository;
    @Mock
    private JpaProfessionalRepository jpaProfessionalRepository;
    @Mock
    private JpaServiceTypeRepository jpaServiceTypeRepository;

    @InjectMocks
    private AppointmentCreatorService service;

    private LocalDateTime start;
    private LocalDateTime end;
    private User user;
    private Professional professional;
    private ServiceType serviceType;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = start.plusHours(1);
        user = new User(1L, "juan", "juan@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        professional = new Professional("Dr. House", "Clinica", true);
        professional.setId(2L);
        serviceType = new ServiceType("Masaje", 60, new BigDecimal("100.00"));
        serviceType.setId(3L);
    }

    private AppointmentRequest request() {
        return new AppointmentRequest(start, end, "una nota", 1L, 2L, 3L);
    }

    @Test
    void create_persistsAppointmentAsUnconfirmedWithCreatedAt() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.of(serviceType));
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfter(2L, end, start))
                .thenReturn(false);
        when(jpaAppointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = service.create(request());

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(jpaAppointmentRepository).save(captor.capture());
        Appointment saved = captor.getValue();

        assertThat(saved.getStartTime()).isEqualTo(start);
        assertThat(saved.getEndTime()).isEqualTo(end);
        assertThat(saved.getNotes()).isEqualTo("una nota");
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getProfessional()).isSameAs(professional);
        assertThat(saved.getServiceType()).isSameAs(serviceType);
        assertThat(saved.getConfirmed()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(result).isSameAs(saved);
    }

    @Test
    void create_rejectsRangeWhenEndIsNotAfterStart() {
        AppointmentRequest req = new AppointmentRequest(start, start, "nota", 1L, 2L, 3L);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endTime must be after startTime");

        verify(jpaAppointmentRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenUserNotFound() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(UserNotFoundException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenProfessionalNotFound() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(ProfessionalNotFoundException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenServiceTypeNotFound() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(ServiceTypeNotFoundException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenProfessionalHasOverlappingAppointment() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.of(serviceType));
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfter(2L, end, start))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(AppointmentOverlapException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }
}
