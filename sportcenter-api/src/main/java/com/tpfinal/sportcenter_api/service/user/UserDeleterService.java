package com.tpfinal.sportcenter_api.service.user;

import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

@Service
public class UserDeleterService {
    private final JpaUserRepository jpaUserRepository;
    private final UserFinderService userFinderService;

    public UserDeleterService(JpaUserRepository jpaUserRepository, UserFinderService userFinderService) {
        this.jpaUserRepository = jpaUserRepository;
        this.userFinderService = userFinderService;
    }

    public void delete(Long id){
        User user = userFinderService.find(id);
        jpaUserRepository.delete(user);
    }
}
