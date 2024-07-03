package com.example.userservice.user.dto;

import com.example.userservice.user.vo.ResponseOrder;
import lombok.*;

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

    private String createdAt;

    private String encryptedPwd;

    private List<ResponseOrder> orders;
}
