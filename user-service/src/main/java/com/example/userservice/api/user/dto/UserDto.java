package com.example.userservice.api.user.dto;

import com.example.userservice.api.user.vo.ResponseOrder;
import com.example.userservice.api.user.vo.ResponseUserAddress;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /** 사용자 PK */
    private Long id;

    /** 사용자 ID */
    private String userId;

    /** 사용자 이름 */
    private String name;

    /** 사용자 비밀번호 */
    private String pwd;

    /** 사용자 이메일 */
    private String email;

    /** 사용자 계정 생성일시 */
    private LocalDateTime createdAt;

    /** 사용자 비밀번호 */
    private String password;

    /** 사용자 주문 목록 */
    private List<ResponseOrder> orders;

    /** 사용자 주소 목록 */
    private List<ResponseUserAddress> addresses;

    /**
     * QueryDSL @QueryProjection 생성자
     *
     * @param id 사용자 PK
     * @param userId 사용자 ID
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @param createdAt 사용자 계정 생성일시
     */
    @QueryProjection
    public UserDto(
            Long id,
            String userId,
            String name,
            String email,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }
}
