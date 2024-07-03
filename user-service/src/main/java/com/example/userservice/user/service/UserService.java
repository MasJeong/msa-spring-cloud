package com.example.userservice.user.service;

import com.example.userservice.user.domain.UserEntity;
import com.example.userservice.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);

    List<UserEntity> getAllUsers();
}
