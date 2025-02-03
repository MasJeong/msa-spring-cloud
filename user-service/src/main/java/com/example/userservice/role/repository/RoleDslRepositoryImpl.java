package com.example.userservice.role.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.userservice.role.domain.QRoleEntity.roleEntity;
import static com.example.userservice.role.domain.QUserRoleEntity.userRoleEntity;

@RequiredArgsConstructor
public class RoleDslRepositoryImpl implements RoleDslRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자 역할명 목록 조회
     * @param userId 사용자 UUID
     * @return 사용자 역할명 목록
     */
    @Override
    public List<String> findRoleNamesByUserId(String userId) {
        return queryFactory
                .select(roleEntity.roleName)
                .from(userRoleEntity)
                .join(roleEntity).on(roleEntity.roleId.eq(userRoleEntity.role.roleId))
                .where(userRoleEntity.user.userId.eq(userId))
                .fetch();
    }
}
