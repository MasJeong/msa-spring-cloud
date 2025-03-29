package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.Role;
import com.example.userservice.api.role.domain.UserRole;
import com.example.userservice.api.user.domain.User;
import com.example.userservice.api.user.repository.UserRepository;
import com.example.userservice.com.support.RepositoryTestSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRoleRepositoryRepositoryTest extends RepositoryTestSupport {

    @PersistenceContext
    EntityManager em;

    /** 사용자 repository */
    @Autowired
    private UserRepository userRepository;

    /** 역할 repository */
    @Autowired
    private RoleRepository roleRepository;

    /** 사용자 역할 repository */
    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUpUserRoleEntity() {

    }

    @Test
    @DisplayName("사용자 역할 등록 테스트")
    public void testCreateUserRole() {
        // given
        User saveUser = userRepository.save(User.builder()
                .name("name")
                .email("email")
                .password("password")
                .userId(UUID.randomUUID().toString())
                .build());

        Role saveRole = roleRepository.save(Role.builder()
                .roleId(UUID.randomUUID().toString())
                .roleName("ADMIN")
                .description("description")
                .build());

        UserRole userRole = UserRole.builder()
                .roleId(saveRole.getRoleId())
                .userId(saveUser.getUserId())
                .build();

        // when
        userRoleRepository.save(userRole);

        em.flush();
        em.clear();

        assert userRole.getId() != null;
        Optional<UserRole> opUserRole = userRoleRepository.findById(userRole.getId());
        UserRole findUserRole = opUserRole.orElseThrow(() ->
                new EntityNotFoundException("UserRole not found with id: " + userRole.getId()));

        // then
        assertThat(findUserRole).isNotNull();
        assertThat(findUserRole.getId()).isEqualTo(userRole.getId());
    }

    @Test
    @DisplayName("사용자 역할 제약조건 테스트")
    public void testUniqueConstraintViolation() {
        // given
        User saveUser = userRepository.save(User.builder()
                .userId(UUID.randomUUID().toString())
                .name("name")
                .email("email")
                .password("password")
                .build());

        Role saveRole = roleRepository.save(Role.builder()
                .roleId(UUID.randomUUID().toString())
                .roleName("ADMIN")
                .description("description")
                .build());

        UserRole userRole = UserRole.builder()
                .roleId(saveRole.getRoleId())
                .userId(saveUser.getUserId())
                .build();

        userRoleRepository.save(userRole);

        UserRole secondUserRole = UserRole.builder()
                .userId(userRole.getUserId())
                .roleId(userRole.getRoleId())
                .build();

        // when then
        assertThrows(DataIntegrityViolationException.class, () -> userRoleRepository.saveAndFlush(secondUserRole));
    }

    @Test
    public void testDeleteUserRole() {
        // given
        User saveUser = userRepository.save(User.builder()
                .name("name")
                .email("email")
                .password("password")
                .userId(UUID.randomUUID().toString())
                .build());

        Role saveRole = roleRepository.save(Role.builder()
                .roleId(UUID.randomUUID().toString())
                .roleName("ADMIN")
                .description("description")
                .build());

        UserRole userRole = UserRole.builder()
                .roleId(saveRole.getRoleId())
                .userId(saveUser.getUserId())
                .build();

        userRoleRepository.save(userRole);

        em.flush();
        em.clear();

        assert userRole.getId() != null;
        UserRole findUuserRole = userRoleRepository.findById(userRole.getId()).orElse(null);

        // when
        assert findUuserRole != null;
        userRoleRepository.delete(findUuserRole);

        em.flush();
        em.clear();

        UserRole deletedUserRole = userRoleRepository.findById(userRole.getId()).orElse(null);

        // then
        assertThat(deletedUserRole).isNull();
    }

}