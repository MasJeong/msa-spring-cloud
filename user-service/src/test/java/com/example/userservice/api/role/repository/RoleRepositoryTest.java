package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.Role;
import com.example.userservice.com.support.TestSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest extends TestSupport {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("역할 저장 및 상세 조회")
    void testSaveAndFindRole() {
        // given
        Role role = Role.builder()
                .roleId(UUID.randomUUID().toString())
                .roleName("ADMIN")
                .description("description")
                .build();

        Role saveRole = roleRepository.save(role);

        em.flush();
        em.clear();

        assert saveRole.getId() != null;

        // when
        Optional<Role> opRole = roleRepository.findById(saveRole.getId());
        Role findRole = opRole.orElseThrow(() ->
                new EntityNotFoundException("Role not found with id: " + saveRole.getId()));

        // then
        assertNotNull(findRole);
        assertEquals(role.getId(), findRole.getId());
        assertEquals(role.getRoleId(), findRole.getRoleId());
        assertEquals(role.getRoleName(), findRole.getRoleName());
        assertEquals(role.getDescription(), findRole.getDescription());
    }

}