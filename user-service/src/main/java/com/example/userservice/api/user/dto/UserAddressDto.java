package com.example.userservice.api.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자 주소 조회용 DTO (QueryDSL 프로젝션용)
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserAddressDto {

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

    /**
     * QueryDSL @QueryProjection 생성자
     *
     * @param id 주소 ID
     * @param addressName 주소명
     * @param recipientName 수령인명
     * @param zipCode 우편번호
     * @param baseAddress 기본 주소
     * @param detailAddress 상세 주소
     * @param phoneNumber 연락처
     * @param isDefault 기본 배송지 여부
     * @param createdAt 생성일시
     */
    @QueryProjection
    public UserAddressDto(
            Long id,
            String addressName,
            String recipientName,
            String zipCode,
            String baseAddress,
            String detailAddress,
            String phoneNumber,
            Boolean isDefault,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.addressName = addressName;
        this.recipientName = recipientName;
        this.zipCode = zipCode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.phoneNumber = phoneNumber;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }
}
