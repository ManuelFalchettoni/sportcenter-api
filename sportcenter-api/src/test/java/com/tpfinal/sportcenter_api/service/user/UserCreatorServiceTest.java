package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Activa Mockito: procesa @Mock e @InjectMocks antes de cada test.
@ExtendWith(MockitoExtension.class)
class UserCreatorServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder; // hashea la contraseña

    @InjectMocks
    private UserCreatorService service;

    // Caso feliz: normaliza datos, hashea la password y fuerza el rol USER.
    @Test
    void create_normalizesHashesPasswordAndForcesUserRole() {
        // Mandamos username y email con espacios/mayúsculas para probar la
        // normalización (trim al username, trim + lowercase al email).
        UserRequest request = new UserRequest("  Juan  ", "  JUAN@Mail.com ", "secret123");
        // Que no exista ningún usuario con ese username ni ese email (ya normalizados).
        when(jpaUserRepository.existsByUsername("Juan")).thenReturn(false);
        when(jpaUserRepository.existsByEmail("juan@mail.com")).thenReturn(false);
        // El encoder devuelve un hash simulado para la contraseña.
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashed");
        // save devuelve el mismo usuario que recibe.
        when(jpaUserRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        User result = service.create(request);

        // Capturamos el usuario que se mandó a guardar para revisar sus campos.
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(jpaUserRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("Juan");            // sin espacios
        assertThat(saved.getEmail()).isEqualTo("juan@mail.com");      // minúsculas y sin espacios
        assertThat(saved.getPassword()).isEqualTo("$2a$10$hashed");   // guardado hasheado, no en claro
        assertThat(saved.getRole()).isEqualTo(UserEnum.USER);         // rol forzado a USER
        assertThat(saved.getCreatedDate()).isNotNull();               // con fecha de alta
        assertThat(result).isSameAs(saved);
    }

    // Si la contraseña viene en blanco (solo espacios), se rechaza.
    @Test
    void create_throwsWhenPasswordIsBlank() {
        UserRequest request = new UserRequest("juan", "juan@mail.com", "   ");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("password must not be blank");

        verify(jpaUserRepository, never()).save(any()); // no guarda
    }

    // Username ya tomado -> conflicto, no se guarda.
    @Test
    void create_throwsWhenUsernameAlreadyExists() {
        UserRequest request = new UserRequest("juan", "juan@mail.com", "secret123");
        when(jpaUserRepository.existsByUsername("juan")).thenReturn(true); // ya existe

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("username"); // el mensaje aclara qué campo chocó

        verify(jpaUserRepository, never()).save(any());
    }

    // Email ya tomado -> conflicto. El username pasa, pero el email no.
    @Test
    void create_throwsWhenEmailAlreadyExists() {
        UserRequest request = new UserRequest("juan", "juan@mail.com", "secret123");
        when(jpaUserRepository.existsByUsername("juan")).thenReturn(false);
        when(jpaUserRepository.existsByEmail("juan@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(jpaUserRepository, never()).save(any());
    }
}
