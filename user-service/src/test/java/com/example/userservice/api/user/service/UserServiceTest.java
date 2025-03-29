package com.example.userservice.api.user.service;

import com.example.userservice.api.user.domain.User;
import com.example.userservice.api.user.dto.UserDto;
import com.example.userservice.api.user.repository.UserRepository;
import com.example.userservice.com.support.ServiceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceTest extends ServiceTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

}