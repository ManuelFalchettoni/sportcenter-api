package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentFilterRequest;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test unitario con Mockito puro: sin Spring ni base de datos.
// La traducción exacta de cada Specification a SQL es responsabilidad de
// Spring Data; acá verificamos la lógica del servicio: validación del rango,
// que la consulta se haga por specification y el passthrough de la página.
@ExtendWith(MockitoExtension.class)
class AppointmentGetAllServiceTest {

    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;

    @InjectMocks
    private AppointmentGetAllService service;

    private static final Pageable PAGEABLE = PageRequest.of(0, 20);

    private static final AppointmentFilterRequest NO_FILTERS =
            new AppointmentFilterRequest(null, null, null, null, null);

    private User adminCaller() {
        return new User(1L, "admin", "admin@example.com", "hash", UserEnum.ADMIN,
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    private User regularCaller() {
        return new User(7L, "manu", "manu@example.com", "hash", UserEnum.USER,
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    // El servicio consulta por specification y devuelve la página tal cual.
    @Test
    void findAll_queriesBySpecificationAndReturnsPage() {
        Page<Appointment> page = new PageImpl<>(List.of(new Appointment()));
        when(jpaAppointmentRepository.findAll(any(Specification.class), eq(PAGEABLE)))
                .thenReturn(page);

        Page<Appointment> result = service.findAll(PAGEABLE, adminCaller(), NO_FILTERS);

        assertEquals(page, result);
        verify(jpaAppointmentRepository).findAll(any(Specification.class), eq(PAGEABLE));
    }

    // Con filtros (USER + rango + estado + profesional + query) también consulta
    // por specification: las condiciones se combinan en una sola consulta.
    @Test
    void findAll_acceptsAllFiltersCombined() {
        AppointmentFilterRequest filter = new AppointmentFilterRequest(
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 7, 31, 23, 59),
                AppointmentStatusEnum.PENDING,
                2L,
                "kinesio");
        when(jpaAppointmentRepository.findAll(any(Specification.class), eq(PAGEABLE)))
                .thenReturn(Page.empty());

        service.findAll(PAGEABLE, regularCaller(), filter);

        verify(jpaAppointmentRepository).findAll(any(Specification.class), eq(PAGEABLE));
    }

    // from posterior a to -> 400 (IllegalArgumentException) sin tocar la base.
    @Test
    void findAll_throwsWhenFromIsAfterTo() {
        AppointmentFilterRequest filter = new AppointmentFilterRequest(
                LocalDateTime.of(2026, 7, 31, 0, 0),
                LocalDateTime.of(2026, 7, 1, 0, 0),
                null, null, null);

        assertThrows(IllegalArgumentException.class,
                () -> service.findAll(PAGEABLE, adminCaller(), filter));

        verify(jpaAppointmentRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}
