package com.example.userservice.role.repository;

import com.example.userservice.com.support.TestSupport;
import com.example.userservice.role.domain.RoleEntity;
import jakarta.persistence.EntityManager;
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
        RoleEntity roleEntity = RoleEntity.builder()
                .roleId(UUID.randomUUID().toString())
                .name("ADMIN")
                .description("description")
                .build();

        RoleEntity saveRole = roleRepository.save(roleEntity);

        em.flush();
        em.clear();

        // when
        Optional<RoleEntity> opRole = roleRepository.findById(saveRole.getId());
        RoleEntity findRoleEntity = opRole.orElseThrow(() -> new RuntimeException("Role not found"));

        // then
        assertNotNull(findRoleEntity);
        assertEquals(roleEntity.getId(), findRoleEntity.getId());
        assertEquals(roleEntity.getRoleId(), findRoleEntity.getRoleId());
        assertEquals(roleEntity.getName(), findRoleEntity.getName());
        assertEquals(roleEntity.getDescription(), findRoleEntity.getDescription());
    }

}