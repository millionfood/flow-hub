package com.ajh.flow.service;

import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.common.exception.DuplicateEntityException;
import com.ajh.flow.domain.User;
import com.ajh.flow.dto.user.UserRegisterDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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

    @Test
    @DisplayName("회원가입이 정상적으로 이루어져야 한다.")
    public void registerUser () throws Exception{
        //Given
        Long userId =  userService.registerUser(new UserRegisterDto("millionfood@naver.com","12312312312","안진혁", UserRole.USER));
        //When
        em.flush();
        em.clear();
        //Then
        assertThat(userService.findById(userId)).isNotNull();
        assertThat(userService.findById(userId).getEmail()).isEqualTo("millionfood@naver.com");
        assertThat(userService.findById(userId).getName()).isEqualTo("안진혁");
    }
    @Test
    @DisplayName("이메일 중복시 회원가입이 되어서는 안된다.")
    public void duplicateEmail() throws Exception{
        //Given
        userService.registerUser(new UserRegisterDto("millionfood@naver.com","12312312312","안진혁", UserRole.USER));
        //When
        em.flush();
        em.clear();
        //Then
        UserRegisterDto newDto = new UserRegisterDto("millionfood@naver.com","1111","안태웅", UserRole.USER);
        assertThrows(DuplicateEntityException.class,()->userService.registerUser(newDto));
;    }
}