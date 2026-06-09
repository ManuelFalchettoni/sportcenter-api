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

// Activa Mockito: procesa los @Mock y el @InjectMocks antes de cada test.
@ExtendWith(MockitoExtension.class)
class AppointmentCreatorServiceTest {

    // El servicio de creación depende de cuatro repositorios. Los reemplazamos
    // por mocks para probar la lógica del servicio sin tocar la base de datos.
    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;
    @Mock
    private JpaUserRepository jpaUserRepository;
    @Mock
    private JpaProfessionalRepository jpaProfessionalRepository;
    @Mock
    private JpaServiceTypeRepository jpaServiceTypeRepository;

    // Objeto bajo prueba: Mockito le inyecta los cuatro mocks de arriba.
    @InjectMocks
    private AppointmentCreatorService service;

    // Datos compartidos por los tests; se rearman antes de cada uno en setUp().
    private LocalDateTime start;
    private LocalDateTime end;
    private User user;
    private Professional professional;
    private ServiceType serviceType;

    // @BeforeEach corre antes de CADA test, así cada uno arranca con datos
    // frescos y no se contaminan entre sí.
    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1); // un horario a futuro
        end = start.plusHours(1);                // que termina 1 hora después
        user = new User(1L, "juan", "juan@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        professional = new Professional("Dr. House", "Clinica", true);
        professional.setId(2L);
        serviceType = new ServiceType("Masaje", 60, new BigDecimal("100.00"));
        serviceType.setId(3L);
    }

    // Helper para no repetir la construcción del request en cada test.
    // Los ids (1, 2, 3) coinciden con los de user, professional y serviceType.
    private AppointmentRequest request() {
        return new AppointmentRequest(start, end, "una nota", 1L, 2L, 3L);
    }

    // Caso feliz: con datos válidos, el turno se guarda como NO confirmado y con
    // fecha de creación seteada.
    @Test
    void create_persistsAppointmentAsUnconfirmedWithCreatedAt() {
        // Arrange: que las tres búsquedas encuentren sus entidades...
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.of(serviceType));
        // ...y que no exista solapamiento (false = el horario está libre).
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndCancelledFalse(2L, end, start))
                .thenReturn(false);
        // save(...) devuelve el mismo objeto que recibe (simulamos el guardado).
        // getArgument(0) = el primer argumento con el que se llamó a save.
        when(jpaAppointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: ejecutamos lo que queremos probar.
        Appointment result = service.create(request());

        // Assert: capturamos el turno que el servicio le pasó a save() para
        // inspeccionar sus campos. ArgumentCaptor "atrapa" ese argumento.
        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(jpaAppointmentRepository).save(captor.capture()); // se llamó a save 1 vez
        Appointment saved = captor.getValue();                   // el turno capturado

        // Verificamos que el servicio armó el turno con los datos del request...
        assertThat(saved.getStartTime()).isEqualTo(start);
        assertThat(saved.getEndTime()).isEqualTo(end);
        assertThat(saved.getNotes()).isEqualTo("una nota");
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getProfessional()).isSameAs(professional);
        assertThat(saved.getServiceType()).isSameAs(serviceType);
        assertThat(saved.getConfirmed()).isFalse();      // nace sin confirmar
        assertThat(saved.getCreatedAt()).isNotNull();    // y con fecha de alta
        // ...y que devuelve ese mismo turno guardado.
        assertThat(result).isSameAs(saved);
    }

    // Validación: si endTime no es posterior a startTime, se rechaza.
    @Test
    void create_rejectsRangeWhenEndIsNotAfterStart() {
        // start == start: rango inválido (dura 0 minutos).
        AppointmentRequest req = new AppointmentRequest(start, start, "nota", 1L, 2L, 3L);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endTime must be after startTime");

        // never() = el guardado NO debe ocurrir si la validación falló.
        verify(jpaAppointmentRepository, never()).save(any());
    }

    // Si el usuario no existe, se corta antes de guardar.
    @Test
    void create_throwsWhenUserNotFound() {
        // Optional.empty() simula "no encontrado".
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(UserNotFoundException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    // Si el profesional no existe, se corta antes de guardar.
    @Test
    void create_throwsWhenProfessionalNotFound() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(ProfessionalNotFoundException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    // Si el tipo de servicio no existe, se corta antes de guardar.
    @Test
    void create_throwsWhenServiceTypeNotFound() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(ServiceTypeNotFoundException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }

    // Doble reserva: si el profesional ya tiene un turno solapado, se rechaza.
    @Test
    void create_throwsWhenProfessionalHasOverlappingAppointment() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jpaProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(jpaServiceTypeRepository.findById(3L)).thenReturn(Optional.of(serviceType));
        // true = ya hay un turno que pisa este horario.
        when(jpaAppointmentRepository.existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndCancelledFalse(2L, end, start))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(AppointmentOverlapException.class);

        verify(jpaAppointmentRepository, never()).save(any());
    }
}
