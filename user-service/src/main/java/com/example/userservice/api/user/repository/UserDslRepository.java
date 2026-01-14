package com.example.userservice.api.user.repository;

import com.example.userservice.api.user.dto.UserAddressDto;
import com.example.userservice.api.user.dto.UserDto;

import java.util.List;

public interface UserDslRepository {

    /**
     * 사용자 정보 조회 (DTO)
     *
     * @param userId 사용자 ID (UUID)
     * @return 사용자 DTO
     */
    UserDto findUserDtoByUserId(String userId);

    /**
     * 사용자 주소 목록 조회 (DTO) - 단일 사용자
     *
     * @param userId 사용자 PK
     * @return 사용자 주소 DTO 목록
     */
    List<UserAddressDto> findUserAddressDtoListByUserId(Long userId);
}
