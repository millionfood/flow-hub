package com.ajh.flow.service;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.common.exception.DuplicateEntityException;
import com.ajh.flow.dto.user.UserRegisterDto;
import com.ajh.flow.dto.user.UserUpdateDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager em;

    Long userId;
    @BeforeEach
    void setUp() {
        userId =  userService.registerUser(new UserRegisterDto("millionfood@naver.com","12312312312","안진혁", UserRole.USER,"01038041915"));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("회원가입이 정상적으로 이루어져야 한다.")
    public void registerUser () throws Exception{
        //Given
        //When
        //Then
        assertThat(userService.findDetailDtoById(userId)).isNotNull();
        assertThat(userService.findDetailDtoById(userId).getEmail()).isEqualTo("millionfood@naver.com");
        assertThat(userService.findDetailDtoById(userId).getName()).isEqualTo("안진혁");
    }
    @Test
    @DisplayName("이메일 중복시 회원가입이 되어서는 안된다.")
    public void duplicateEmail() throws Exception{
        //Given
        //When
        //Then
        UserRegisterDto newDto = new UserRegisterDto("millionfood@naver.com","1111","안태웅", UserRole.USER,"01038041915");
        assertThrows(DuplicateEntityException.class,()->userService.registerUser(newDto));
;    }
    @Test
    @DisplayName("회원정보 수정이 정상적으로 이루어져야 한다.")
    public void editProfile() throws Exception{
        //Given
        //When
        UserUpdateDto updateDto = new UserUpdateDto("안태웅","01038041915","2222");
        userService.editUserInfo(userId,updateDto);
        em.flush();
        em.clear();
        //Then
        assertThat(userService.findDetailDtoById(userId).getName()).isEqualTo(updateDto.getName());
        assertThat(userService.findDetailDtoById(userId).getPassword()).isEqualTo(updateDto.getPassword());

    }
    @Test
    @DisplayName("사용자 계정의 사용, 미사용 상태 변경이 정상적으로 이루어져야 한다.")
    public void changeUserStatus() throws Exception{
        //Given

        //When
        userService.stopUser(userId);
        em.flush();
        em.clear();
        //Then
        assertThat(userService.findById(userId).getUseYn()).isEqualTo(UseYn.N);
        //When
        userService.resumeUser(userId);
        em.flush();
        em.clear();
        //Then
        assertThat(userService.findById(userId).getUseYn()).isEqualTo(UseYn.Y);

    }
}