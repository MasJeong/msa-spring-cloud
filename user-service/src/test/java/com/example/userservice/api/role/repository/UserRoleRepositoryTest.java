package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.Role;
import com.example.userservice.api.role.domain.UserRole;
import com.example.userservice.api.user.domain.User;
import com.example.userservice.api.user.repository.UserRepository;
import com.example.userservice.com.support.TestSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
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
public class UserRoleRepositoryTest extends TestSupport {

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

//    @Test
//    public void testReadUserRole() {
//        User user = new User();
//        user.setId("user2");
//        user.setUsername("testUser2");
//        userRepository.save(user);
//
//        Role role = new Role();
//        role.setId("role2");
//        role.setName("USER");
//        roleRepository.save(role);
//
//        UserRoleId id = new UserRoleId("role2", "user2");
//        UserRole userRole = new UserRole();
//        userRole.setId(id);
//        userRole.setUser(user);
//        userRole.setRole(role);
//
//        entityManager.persist(userRole);
//        entityManager.flush();
//
//        UserRole found = userRoleRepository.findById(id).orElse(null);
//
//        assertThat(found).isNotNull();
//        assertThat(found.getUser().getUsername()).isEqualTo("testUser2");
//        assertThat(found.getRole().getName()).isEqualTo("USER");
//    }
//
//    @Test
//    public void testUpdateUserRole() {
//        User user = new User();
//        user.setId("user3");
//        user.setUsername("testUser3");
//        userRepository.save(user);
//
//        Role role1 = new Role();
//        role1.setId("role3");
//        role1.setName("MANAGER");
//        roleRepository.save(role1);
//
//        UserRoleId id = new UserRoleId("role3", "user3");
//        UserRole userRole = new UserRole();
//        userRole.setId(id);
//        userRole.setUser(user);
//        userRole.setRole(role1);
//
//        entityManager.persist(userRole);
//        entityManager.flush();
//
//        Role role2 = new Role();
//        role2.setId("role4");
//        role2.setName("ADMIN");
//        roleRepository.save(role2);
//
//        UserRole found = userRoleRepository.findById(id).orElse(null);
//        assertThat(found).isNotNull();
//        found.setRole(role2);
//        userRoleRepository.save(found);
//
//        UserRole updated = userRoleRepository.findById(id).orElse(null);
//        assertThat(updated).isNotNull();
//        assertThat(updated.getRole().getName()).isEqualTo("ADMIN");
//    }
//
//    @Test
//    public void testDeleteUserRole() {
//        User user = new User();
//        user.setId("user4");
//        user.setUsername("testUser4");
//        userRepository.save(user);
//
//        Role role = new Role();
//        role.setId("role5");
//        role.setName("GUEST");
//        roleRepository.save(role);
//
//        UserRoleId id = new UserRoleId("role5", "user4");
//        UserRole userRole = new UserRole();
//        userRole.setId(id);
//        userRole.setUser(user);
//        userRole.setRole(role);
//
//        entityManager.persist(userRole);
//        entityManager.flush();
//
//        userRoleRepository.deleteById(id);
//
//        UserRole deleted = userRoleRepository.findById(id).orElse(null);
//        assertThat(deleted).isNull();
//    }

}