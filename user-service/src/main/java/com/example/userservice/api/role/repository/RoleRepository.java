package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleDslRepository {

}
