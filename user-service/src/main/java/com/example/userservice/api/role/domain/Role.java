package com.example.userservice.api.role.domain;

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
public class Role extends BaseDomain implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 역할 ID */
    @Column(name = "ROLE_ID", nullable = false, unique = true)
    private String roleId;

    /** 역할명 */
    @Column(length = 20, nullable = false, unique = true)
    private String roleName;

    /** 역할설명 */
    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRole> userRoles = new ArrayList<>();

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}
