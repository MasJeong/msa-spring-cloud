package com.example.userservice.api.role.domain;

import com.example.userservice.com.domain.BaseDomain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "user_roles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole extends BaseDomain implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "role_id", nullable = false)
    private String roleId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Role role;

    @Size(max = 36)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}