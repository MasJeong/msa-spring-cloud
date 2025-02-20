package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>, RoleDslRepository {

}
