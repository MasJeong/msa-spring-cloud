package com.example.userservice.api.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 주소 응답 VO (API 응답용)
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUserAddress {

    /** 주소 ID */
    private Long id;

    /** 주소명 (예: 집, 회사) */
    private String addressName;

    /** 수령인명 */
    private String recipientName;

    /** 우편번호 */
    private String zipCode;

    /** 기본 주소 */
    private String baseAddress;

    /** 상세 주소 */
    private String detailAddress;

    /** 연락처 */
    private String phoneNumber;

    /** 기본 배송지 여부 */
    private Boolean isDefault;

    /** 생성일시 */
    private LocalDateTime createdAt;
}
