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

@ExtendWith(MockitoExtension.class)
class UserCreatorServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCreatorService service;

    @Test
    void create_normalizesHashesPasswordAndForcesUserRole() {
        UserRequest request = new UserRequest("  Juan  ", "  JUAN@Mail.com ", "secret123");
        when(jpaUserRepository.existsByUsername("Juan")).thenReturn(false);
        when(jpaUserRepository.existsByEmail("juan@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashed");
        when(jpaUserRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.create(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(jpaUserRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("Juan");
        assertThat(saved.getEmail()).isEqualTo("juan@mail.com");
        assertThat(saved.getPassword()).isEqualTo("$2a$10$hashed");
        assertThat(saved.getRole()).isEqualTo(UserEnum.USER);
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(result).isSameAs(saved);
    }

    @Test
    void create_throwsWhenPasswordIsBlank() {
        UserRequest request = new UserRequest("juan", "juan@mail.com", "   ");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("password must not be blank");

        verify(jpaUserRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenUsernameAlreadyExists() {
        UserRequest request = new UserRequest("juan", "juan@mail.com", "secret123");
        when(jpaUserRepository.existsByUsername("juan")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("username");

        verify(jpaUserRepository, never()).save(any());
    }

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
