package com.ajh.flow.dto.user;

import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserRegisterDto {
    //테스트용
    public UserRegisterDto(String email,String password, String name, UserRole role, String tel) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.tel = tel;
    }

    @NotNull(message = "email은 필수 입력 값입니다.")
    @Email(message = "올바른 Email 형식이 아닙니다.")
    private String email;
    @NotNull(message = "password는 필수 입력 값입니다.")
    private String password;
    @NotNull(message = "name은 필수 입력 값입니다.")
    private String name;
    @NotNull
    @Pattern(
            regexp = "^\\d{11}$",
            message = "올바른 전화번호 11자리를 입력해 주세요"
    )
    private String tel;
    private UserRole role = UserRole.USER;

    public User toVo() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .role(this.role)
                .tel(this.tel)
                .build();
    }

    public void setEncodedPassword(String password){
        this.password = password;
    }
}
