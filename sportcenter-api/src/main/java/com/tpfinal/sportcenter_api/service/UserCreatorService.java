package com.tpfinal.sportcenter_api.service;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.entity.User;
import com.tpfinal.sportcenter_api.repository.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreatorService {
    private final JpaUserRepository jpaUserRepository;
    private final PasswordEncoder passwordEncoder;


    public UserCreatorService(JpaUserRepository jpaUserRepository, PasswordEncoder passwordEncoder) {
        this.jpaUserRepository = jpaUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(UserRequest request){
        User user = UserRequest.fromRequest(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User create = jpaUserRepository.save(user);
        return create;
    }
}

