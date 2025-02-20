package com.example.userservice.api.user.domain;

import com.example.userservice.com.domain.BaseDomain;
import com.example.userservice.api.role.domain.UserRoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseDomain implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 사용자 ID */
    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    /** 사용자명 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 이메일 */
    @Column(nullable = false, length = 50, unique = true)
    private String email;

    /** 사용자 패스워드 */
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRoleEntity> userRoles = new ArrayList<>();

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}
