package com.ajh.flow.dto.user;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserDetailDto {

    public UserDetailDto(Long id,String email,String password, String name, UserRole role,UseYn useYn) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.useYn =  useYn;
    }
    //엔티티를 dto로 (화면 조회용)
    public UserDetailDto(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.role = user.getRole();
    }

    private Long id;
    private String email;
    private String password;
    private String name;
    private UserRole role;
    private UseYn useYn;

}
