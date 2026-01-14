package com.example.userservice.api.user.repository;

import com.example.userservice.api.user.dto.QUserAddressDto;
import com.example.userservice.api.user.dto.QUserDto;
import com.example.userservice.api.user.dto.UserAddressDto;
import com.example.userservice.api.user.dto.UserDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.userservice.api.user.domain.QUser.user;
import static com.example.userservice.api.user.domain.QUserAddress.userAddress;

@RequiredArgsConstructor
public class UserDslRepositoryImpl implements UserDslRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자 정보 조회 (DTO)
     *
     * @param userId 사용자 ID (UUID)
     * @return 사용자 DTO
     */
    @Override
    public UserDto findUserDtoByUserId(String userId) {
        return queryFactory
                .select(new QUserDto(
                        user.id,
                        user.userId,
                        user.name,
                        user.email,
                        user.createdAt
                ))
                .from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
    }

    /**
     * 사용자 주소 목록 조회 (DTO) - 단일 사용자
     *
     * @param userId 사용자 PK
     * @return 사용자 주소 DTO 목록
     */
    @Override
    public List<UserAddressDto> findUserAddressDtoListByUserId(Long userId) {
        return queryFactory
                .select(new QUserAddressDto(
                        userAddress.id,
                        userAddress.addressName,
                        userAddress.recipientName,
                        userAddress.zipCode,
                        userAddress.baseAddress,
                        userAddress.detailAddress,
                        userAddress.phoneNumber,
                        userAddress.isDefault,
                        userAddress.createdAt
                ))
                .from(userAddress)
                .where(userAddress.userId.eq(userId))
                .fetch();
    }

}
