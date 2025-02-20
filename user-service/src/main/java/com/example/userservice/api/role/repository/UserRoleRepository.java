package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.UserRoleEntity;
import com.example.userservice.api.role.domain.UserRoleEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleEntityPK> {

}
