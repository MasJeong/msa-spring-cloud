package com.example.userservice.api.user.repository;

import com.example.userservice.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserDslRepository {

    /**
     * 사용자 상세 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 상세정보
     */
    User findByUserId(String userId);

    /**
     * 사용자 상세 조회
     *
     * @param email 사용자 이메일
     * @return 사용자 상세정보
     */
    Optional<User> findByEmail(String email);

}
