package com.example.userservice.role.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Builder
@Getter @Setter @EqualsAndHashCode @AllArgsConstructor @NoArgsConstructor
public class UserRoleEntityPK implements Serializable {

    /** 역할 ID */
    private String role;

    /** 사용자 ID */
    private String user;
}
