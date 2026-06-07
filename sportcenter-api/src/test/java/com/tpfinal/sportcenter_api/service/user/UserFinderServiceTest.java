package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.exception.user.UserNotFoundException;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFinderServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private UserFinderService service;

    @Test
    void find_returnsUserWhenExists() {
        User user = new User(1L, "juan", "juan@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(service.find(1L)).isSameAs(user);
    }

    @Test
    void find_throwsWhenNotFound() {
        when(jpaUserRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.find(42L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("42");
    }
}
