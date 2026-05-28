package com.ajh.flow.domain;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.dto.user.UserUpdateDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false,length = 50)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseYn useYn = UseYn.Y;

    @Builder
    public User(Long id, String email, String password, String name, UserRole role, String tel) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.tel = tel;
        this.useYn = UseYn.Y;
    }

    //-----------------비즈니스 로직-----------------
    public void stopUse(){
        this.useYn = UseYn.N;
    }

    public void reUse(){
        this.useYn = UseYn.Y;
    }

    public void update(UserUpdateDto dto){
        this.name = dto.getName();
        this.tel = dto.getTel();
        if(dto.getPassword() != null && !dto.getPassword().isBlank()){
            this.password = dto.getPassword();
        }
    }
}
