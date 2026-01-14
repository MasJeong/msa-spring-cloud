package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleDslRepository {

    /**
     * 역할명으로 역할 조회
     *
     * @param roleName 역할명
     * @return 역할
     */
    Optional<Role> findByRoleName(String roleName);
}
