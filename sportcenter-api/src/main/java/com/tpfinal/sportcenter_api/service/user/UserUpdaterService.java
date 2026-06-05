package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;

import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import com.tpfinal.sportcenter_api.service.user.UserFinderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserUpdaterService {
    private final JpaUserRepository jpaUserRepository;
    private final UserFinderService userFinderService;
    private final PasswordEncoder passwordEncoder;

    public UserUpdaterService(JpaUserRepository jpaUserRepository, UserFinderService userFinderService, PasswordEncoder passwordEncoder) {
        this.jpaUserRepository = jpaUserRepository;
        this.userFinderService = userFinderService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse update(Long id, @Valid UserRequest request){
        User user = userFinderService.find(id);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setCreatedDate(request.getCreatedDate());

        if (request.getPassword() != null && !request.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        User updated = jpaUserRepository.save(user);
        return UserResponse.toResponse(updated);
    }
}
