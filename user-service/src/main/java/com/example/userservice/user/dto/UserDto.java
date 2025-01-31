package com.example.userservice.user.dto;

import com.example.userservice.user.vo.ResponseOrder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private LocalDateTime createdAt;

    private String password;

    private List<ResponseOrder> orders;
}
