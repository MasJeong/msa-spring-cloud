package com.example.userservice.api.role.repository;

import java.util.List;

public interface RoleDslRepository {

    /**
     * 사용자 역할 목록 조회
     * @param userId 사용자 UUID
     * @return 사용자 역할 목록
     */
    List<String> findRoleNamesByUserId(String userId);
}
