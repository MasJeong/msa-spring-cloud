package com.example.userservice.user.repository;

import com.example.userservice.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserId(String userId);

    Optional<UserEntity> findByEmail(String email);
}
