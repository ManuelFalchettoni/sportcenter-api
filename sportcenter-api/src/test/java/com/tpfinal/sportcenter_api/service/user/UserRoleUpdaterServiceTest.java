package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRoleUpdateRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleUpdaterServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;
    @Mock
    private UserFinderService userFinderService;

    @InjectMocks
    private UserRoleUpdaterService service;

    @Test
    void updateRole_changesRoleAndPersists() {
        User user = new User(1L, "juan", "juan@mail.com", "hash", UserEnum.USER, LocalDateTime.now());
        when(userFinderService.find(1L)).thenReturn(user);
        when(jpaUserRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = service.updateRole(1L, new UserRoleUpdateRequest(UserEnum.ADMIN));

        assertThat(user.getRole()).isEqualTo(UserEnum.ADMIN);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getRole()).isEqualTo(UserEnum.ADMIN);
    }
}
