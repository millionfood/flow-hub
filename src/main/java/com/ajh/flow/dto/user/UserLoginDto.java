package com.ajh.flow.dto.user;

import jakarta.validation.constraints.NotNull;

public class UserLoginDto {

    @NotNull(message = "email은 필수 입력 값입니다.")
    private String email;
    @NotNull(message = "password는 필수 입력 값입니다.")
    private String password;

}
