package com.example.userservice.api.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {

    private String userId;

    private String name;

    private String email;

    private List<ResponseOrder> orders;
}
