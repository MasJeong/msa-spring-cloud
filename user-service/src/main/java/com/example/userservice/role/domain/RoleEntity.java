package com.example.userservice.role.domain;

import com.example.userservice.com.domain.BaseDomain;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleEntity extends BaseDomain implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 역할 ID */
    @Column(name = "ROLE_ID", nullable = false, unique = true)
    private String roleId;

    /** 역할명 */
    @Column(length = 20, nullable = false)
    private String roleName;

    /** 역할설명 */
    @Column
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRoleEntity> userRoles = new ArrayList<>();

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}
