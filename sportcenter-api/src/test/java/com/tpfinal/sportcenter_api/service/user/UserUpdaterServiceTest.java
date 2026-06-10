package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.security.crypto.password.PasswordEncoder;

// Test unitario con Mockito puro: sin Spring ni base de datos.
@ExtendWith(MockitoExtension.class)
class UserUpdaterServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserFinderService userFinderService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserUpdaterService service;

    private static final String STORED_HASH = "$2a$10$stored-hash";

    private User target;

    @BeforeEach
    void setUp() {
        target = new User(7L, "manu", "manu@example.com", STORED_HASH, UserEnum.USER,
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    private User admin() {
        return new User(1L, "admin", "admin@example.com", "hash", UserEnum.ADMIN,
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    // El propio usuario edita username/email sin tocar la clave: no se pide
    // currentPassword, el rol se preserva y se normaliza (trim/lowercase).
    @Test
    void update_selfProfileWithoutPasswordChange() {
        when(userFinderService.find(7L)).thenReturn(target);
        when(jpaUserRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest request = new UserRequest("  manu.new  ", "  Manu.New@Example.COM ", null);
        UserResponse response = service.update(7L, request, target);

        assertEquals("manu.new", response.getUsername());
        assertEquals("manu.new@example.com", response.getEmail());
        assertEquals(UserEnum.USER, response.getRole());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // El propio usuario cambia su clave confirmando la vigente -> se rehashea.
    @Test
    void update_selfPasswordChangeWithCorrectCurrentPassword() {
        when(userFinderService.find(7L)).thenReturn(target);
        when(passwordEncoder.matches("oldSecret123", STORED_HASH)).thenReturn(true);
        when(passwordEncoder.encode("newSecret123")).thenReturn("$2a$10$new-hash");
        when(jpaUserRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest request = new UserRequest("manu", "manu@example.com", "newSecret123");
        request.setCurrentPassword("oldSecret123");
        service.update(7L, request, target);

        assertEquals("$2a$10$new-hash", target.getPassword());
    }

    // Sin currentPassword -> 400: un token robado no alcanza para tomar la cuenta.
    @Test
    void update_selfPasswordChangeWithoutCurrentPasswordThrows() {
        when(userFinderService.find(7L)).thenReturn(target);

        UserRequest request = new UserRequest("manu", "manu@example.com", "newSecret123");

        assertThrows(IllegalArgumentException.class,
                () -> service.update(7L, request, target));
        verify(jpaUserRepository, never()).save(any(User.class));
    }

    // currentPassword incorrecta -> 400 y no se persiste nada.
    @Test
    void update_selfPasswordChangeWithWrongCurrentPasswordThrows() {
        when(userFinderService.find(7L)).thenReturn(target);
        when(passwordEncoder.matches("wrongOld", STORED_HASH)).thenReturn(false);

        UserRequest request = new UserRequest("manu", "manu@example.com", "newSecret123");
        request.setCurrentPassword("wrongOld");

        assertThrows(IllegalArgumentException.class,
                () -> service.update(7L, request, target));
        verify(jpaUserRepository, never()).save(any(User.class));
    }

    // Un ADMIN resetea la clave de otro sin conocer la vigente (flujo de
    // "olvidé mi contraseña" gestionado por un administrador).
    @Test
    void update_adminResetsPasswordWithoutCurrentPassword() {
        when(userFinderService.find(7L)).thenReturn(target);
        when(passwordEncoder.encode("resetSecret123")).thenReturn("$2a$10$reset-hash");
        when(jpaUserRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest request = new UserRequest("manu", "manu@example.com", "resetSecret123");
        service.update(7L, request, admin());

        assertEquals("$2a$10$reset-hash", target.getPassword());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // username ya tomado por otro -> 409 (comportamiento preexistente).
    @Test
    void update_throwsConflictWhenUsernameIsTaken() {
        when(userFinderService.find(7L)).thenReturn(target);
        when(jpaUserRepository.existsByUsername("taken")).thenReturn(true);

        UserRequest request = new UserRequest("taken", "manu@example.com", null);

        assertThrows(UserAlreadyExistsException.class,
                () -> service.update(7L, request, admin()));
    }
}
