package com.tpfinal.sportcenter_api.repository;

import com.tpfinal.sportcenter_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends
        JpaRepository<User, Long>,
        JpaSpecificationExecutor <User> {
}
