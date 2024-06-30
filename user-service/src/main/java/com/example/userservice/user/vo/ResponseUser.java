package com.example.userservice.user.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUser {

    private String userId;

    private String name;

    private String email;
}
