package com.example.userservice.api.role.domain;

import com.example.userservice.api.user.domain.UserEntity;
import com.example.userservice.com.domain.BaseDomain;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@Entity
@Builder
@Table(name = "user_roles")
@IdClass(UserRoleEntityPK.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoleEntity extends BaseDomain implements Persistable<UserRoleEntityPK> {

    /** 역할 ID */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", nullable = false)
    private RoleEntity role;

    /** 사용자 ID */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID",nullable = false)
    private UserEntity user;

    @Override
    public UserRoleEntityPK getId() {
        return UserRoleEntityPK.builder()
                .role(this.role.getRoleId())
                .user(this.user.getUserId())
                .build();
    }

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}
