package com.ajh.flow.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserUpdateDto {

    public UserUpdateDto(String name,String tel, String password) {
        this.name = name;
        this.tel = tel;
        this.password = password;
    }

    @NotNull
    private String name;
    @NotNull
    private String tel;

    private String password;

    public void setEncodedPassword(String encodedPassword){
        this.password = encodedPassword;
    }

}
