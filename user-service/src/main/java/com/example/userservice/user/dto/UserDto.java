package com.example.userservice.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String userId;

    private String name;

    private String pwd;

    private String email;
}
