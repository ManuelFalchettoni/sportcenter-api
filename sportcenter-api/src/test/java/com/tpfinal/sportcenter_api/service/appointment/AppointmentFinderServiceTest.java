package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

// Imports estáticos: nos dejan llamar a estos métodos sin escribir la clase
// delante (assertThat(...) en vez de Assertions.assertThat(...)).
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

// @ExtendWith(MockitoExtension.class) activa Mockito en JUnit 5: hace que las
// anotaciones @Mock e @InjectMocks de abajo se procesen antes de cada test.
@ExtendWith(MockitoExtension.class)
class AppointmentFinderServiceTest {

    // @Mock crea un "doble de prueba" del repositorio: un objeto falso que no
    // toca la base de datos y cuyo comportamiento definimos nosotros con when(...).
    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;

    // Validador de ownership mockeado: por defecto no tira nada (permite),
    // y en el test de denegación le definimos que rechace.
    @Mock
    private AppointmentOwnershipValidator ownershipValidator;

    // @InjectMocks crea el objeto real que queremos probar (el servicio) y le
    // inyecta los mocks de arriba por su constructor. Así probamos el servicio
    // aislado, sin base de datos real.
    @InjectMocks
    private AppointmentFinderService service;

    // @Test marca un método como caso de prueba que JUnit va a ejecutar.
    @Test
    void find_returnsAppointmentWhenExists() {
        // ----- Arrange (preparar): armamos los datos y el escenario -----
        Appointment appointment = new Appointment(); // turno de ejemplo
        appointment.setId(5L);
        // Stub: "cuando alguien llame a findById(5L), devolvé este turno".
        // Optional.of(...) representa que el dato existe.
        when(jpaAppointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));

        // ----- Act + Assert (actuar y verificar) -----
        // isSameAs comprueba que es exactamente el MISMO objeto (misma referencia),
        // no solo uno igual: confirma que el servicio devuelve lo que dio el repo.
        assertThat(service.find(5L)).isSameAs(appointment);
    }

    @Test
    void find_throwsWhenNotFound() {
        // Arrange: simulamos que el id 99 no existe -> el repo devuelve vacío.
        when(jpaAppointmentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert: al no encontrarlo, el servicio debe lanzar la excepción.
        // assertThatThrownBy ejecuta el código y captura lo que tire para revisarlo.
        assertThatThrownBy(() -> service.find(99L))
                .isInstanceOf(AppointmentNotFoundException.class) // que sea ese tipo
                .hasMessageContaining("99"); // y que el mensaje incluya el id
    }

    // La variante con caller delega el chequeo en el validador de ownership:
    // si este rechaza (caller no es dueño ni ADMIN), el find no devuelve el turno.
    @Test
    void find_withCaller_throwsWhenOwnershipCheckFails() {
        Appointment appointment = new Appointment();
        appointment.setId(5L);
        User intruder = new User(99L, "otro", "otro@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        when(jpaAppointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));
        doThrow(new AccessDeniedException("not the owner"))
                .when(ownershipValidator).check(appointment, intruder);

        assertThatThrownBy(() -> service.find(5L, intruder))
                .isInstanceOf(AccessDeniedException.class);
    }

    // Si el validador permite (dueño o ADMIN), la variante con caller devuelve
    // el mismo turno que el find simple.
    @Test
    void find_withCaller_returnsAppointmentWhenAllowed() {
        Appointment appointment = new Appointment();
        appointment.setId(5L);
        User owner = new User(1L, "juan", "juan@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        when(jpaAppointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));
        // El mock del validador no tira nada por defecto = acceso permitido.

        assertThat(service.find(5L, owner)).isSameAs(appointment);
    }
}
