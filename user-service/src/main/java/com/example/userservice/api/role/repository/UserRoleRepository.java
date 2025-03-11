package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
