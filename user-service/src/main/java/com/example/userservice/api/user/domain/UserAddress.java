package com.example.userservice.api.user.domain;

import com.example.userservice.com.domain.BaseDomain;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@Entity
@Builder
@Table(name = "user_addresses")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAddress extends BaseDomain implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 사용자 PK (FK) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 주소명 (예: 집, 회사) */
    @Column(name = "ADDRESS_NAME", length = 50)
    private String addressName;

    /** 수령인명 */
    @Column(name = "RECIPIENT_NAME", nullable = false, length = 50)
    private String recipientName;

    /** 우편번호 */
    @Column(name = "ZIP_CODE", nullable = false, length = 10)
    private String zipCode;

    /** 기본 주소 */
    @Column(name = "BASE_ADDRESS", nullable = false, length = 200)
    private String baseAddress;

    /** 상세 주소 */
    @Column(name = "DETAIL_ADDRESS", length = 200)
    private String detailAddress;

    /** 연락처 */
    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    /** 기본 배송지 여부 */
    @Column(name = "IS_DEFAULT", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}
