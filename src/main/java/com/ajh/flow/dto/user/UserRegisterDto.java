package com.ajh.flow.dto.user;

import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserRegisterDto {
    //테스트용
    public UserRegisterDto(String email,String password, String name, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }
    //엔티티를 dto로 (화면 조회용)
    public UserRegisterDto(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.role = user.getRole();
    }

    @NotNull(message = "email은 필수 입력 값입니다.")
    @Email(message = "올바른 Email 형식이 아닙니다.")
    private String email;
    @NotNull(message = "password는 필수 입력 값입니다.")
    private String password;
    @NotNull(message = "name은 필수 입력 값입니다.")
    private String name;
    private UserRole role = UserRole.USER;

    public User toVo() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .role(this.role)
                .build();
    }

    public void setEncodedPassword(String password){
        this.password = password;
    }
}
