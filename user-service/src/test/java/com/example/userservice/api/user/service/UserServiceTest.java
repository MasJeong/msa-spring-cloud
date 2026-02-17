package com.example.userservice.api.user.service;

import com.example.userservice.api.role.domain.UserRole;
import com.example.userservice.api.role.repository.UserRoleRepository;
import com.example.userservice.api.user.domain.User;
import com.example.userservice.api.user.domain.UserAddress;
import com.example.userservice.api.user.dto.UserDto;
import com.example.userservice.api.user.repository.UserRepository;
import com.example.userservice.com.support.ServiceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceTest extends ServiceTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    @DisplayName("사용자 등록 테스트")
    void testCreateUser() {
        // Given
        UserDto userDto = UserDto.builder()
                .email("jihun365430@gmail.com")
                .pwd("jihun365430")
                .name("정지훈")
                .build();

        // When
        User savedUser = userService.createUser(userDto);

        User findUser = userRepository.findByUserId(savedUser.getUserId());

        // Then
        assertNotNull(savedUser.getId());
        assertEquals(userDto.getEmail(), findUser.getEmail());
        assertEquals(userDto.getName(), findUser.getName());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 조회 시 역할과 주소를 함께 조회한다")
    void testGetUserByUserId_withRolesAndAddresses() {
        // Given
        String userUuid = UUID.randomUUID().toString();

        User user = User.builder()
                .userId(userUuid)
                .name("테스트유저")
                .email("test-user@example.com")
                .password("password")
                .build();
        User savedUser = userRepository.save(user);

        // 사용자 역할 등록 (UserRole은 userId(UUID)를 통해 사용자와 매핑됨)
        UserRole userRole = UserRole.builder()
                .userId(savedUser.getUserId())
                .roleId(UUID.randomUUID().toString())
                .build();
        userRoleRepository.save(userRole);

        // 사용자 주소 등록 (단방향 관계: User의 addresses에 추가하면 자동으로 user_id FK 설정)
        UserAddress userAddress = UserAddress.builder()
                .addressName("집")
                .recipientName("테스트유저")
                .zipCode("12345")
                .baseAddress("서울특별시 어딘가 123")
                .detailAddress("101동 1001호")
                .phoneNumber("010-0000-0000")
                .isDefault(true)
                .build();
        savedUser.getAddresses().add(userAddress);
        userRepository.save(savedUser); // 주소 저장을 위해 User를 다시 저장

        // When
        UserDto result = userService.getUserByUserId(savedUser.getUserId());

        // Then
        assertNotNull(result);
        assertEquals(savedUser.getUserId(), result.getUserId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getName(), result.getName());
    }

}