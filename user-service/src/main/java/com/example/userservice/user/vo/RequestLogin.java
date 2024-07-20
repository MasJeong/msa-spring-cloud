package com.example.userservice.user.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestLogin {

    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    private String email;

    @NotNull(message = "password cannot be null")
    @Size(min = 8, max = 15, message = "The password can be a minimum of 8 characters and a maximum of 15 characters.")
    private String pwd;

}
