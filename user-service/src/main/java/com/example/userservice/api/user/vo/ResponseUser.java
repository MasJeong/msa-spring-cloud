package com.example.userservice.api.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {

    /** 사용자 ID */
    private String userId;

    /** 사용자명 */
    private String name;

    /** 이메일 */
    private String email;

    /** 주문 목록 */
    private List<ResponseOrder> orders;

    /** 사용자 주소 목록 */
    private List<ResponseUserAddress> userAddresses;
}
