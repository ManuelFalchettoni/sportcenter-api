package com.tpfinal.sportcenter_api.service;

import com.tpfinal.sportcenter_api.entity.User;
import com.tpfinal.sportcenter_api.exception.User.UserNotFoundException;
import com.tpfinal.sportcenter_api.repository.JpaUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserFinderService {
    private final JpaUserRepository jpaUserRepository;

    public UserFinderService(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    public User find(Long id){
        return jpaUserRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException(id));
    }
}
