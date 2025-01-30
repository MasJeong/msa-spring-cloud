package com.example.userservice.role.domain;

import com.example.userservice.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 역할 ID */
    @Column(nullable = false, unique = true)
    private String roleId;

    /** 역할명 */
    @Column(length = 20, unique = true, nullable = false)
    private String name;

    /** 역할설명 */
    @Column
    private String description;

    /** 사용자 연관관계 */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<UserEntity> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleEntity role = (RoleEntity) o;

        return roleId.equals(role.roleId);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
