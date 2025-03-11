package com.example.userservice.api.role.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.userservice.api.role.domain.QRole.role;
import static com.example.userservice.api.role.domain.QUserRole.userRole;

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
                .select(role.roleName)
                .from(userRole)
                .leftJoin(role).on(role.roleId.eq(userRole.role.roleId))
                .where(userRole.user.userId.eq(userId))
                .fetch();
    }
}
