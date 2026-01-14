package com.example.userservice.api.role.repository;

import com.example.userservice.api.role.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * 사용자 ID로 역할 목록 조회
     *
     * @param userId 사용자 ID (UUID)
     * @return 사용자 역할 목록
     */
    List<UserRole> findByUserId(String userId);

    /**
     * 사용자 ID 목록으로 역할 목록 조회 (IN 쿼리)
     * 
     * @apiNote 실무에서 목록 조회 시 사용하는 패턴: IN 쿼리로 한 번에 조회
     *          예: User 100개 조회 → UserRole은 user_id IN (uuid1, uuid2...)으로 한 번에 조회
     * 
     * @param userIds 사용자 ID 목록 (UUID)
     * @return 사용자 역할 목록
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId IN :userIds")
    List<UserRole> findByUserIdIn(@Param("userIds") List<String> userIds);
}
