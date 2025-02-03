package com.example.userservice.role.repository;

import com.example.userservice.role.domain.UserRoleEntity;
import com.example.userservice.role.domain.UserRoleEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleEntityPK> {

}
