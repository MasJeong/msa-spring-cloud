package com.example.userservice.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 사용자 ID */
    @Column(nullable = false, unique = true)
    private String userId;

    /** 사용자명 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 이메일 */
    @Column(nullable = false, length = 50, unique = true)
    private String email;

    /** 사용자 패스워드 */
    @Column(nullable = false)
    private String encryptedPwd;

//    /** 역할 연관관계 */
//    @ManyToMany(fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<RoleEntity> roles = new HashSet<>();
//
//    /**
//     * 역할 정보 세팅
//     * @param role 역할 엔티티
//     */
//    public void addRole(RoleEntity role) {
//        this.roles.add(role);
//    }

}
