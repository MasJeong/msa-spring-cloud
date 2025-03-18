package com.example.userservice.api.user.dto;

import com.example.userservice.api.user.vo.ResponseOrder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

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
}
