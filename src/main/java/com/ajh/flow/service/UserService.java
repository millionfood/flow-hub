package com.ajh.flow.service;

import com.ajh.flow.common.exception.DuplicateEntityException;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.domain.User;
import com.ajh.flow.dto.user.UserDetailDto;
import com.ajh.flow.dto.user.UserRegisterDto;
import com.ajh.flow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //-----------------회원가입-----------------
    @Transactional
    public Long registerUser(UserRegisterDto dto) {

        //이메일 중복은 front와 이중 체크
        validateDuplicateEmail(dto.getEmail());

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setEncodedPassword(encodedPassword);
        //엔티티 생성
        User user = dto.toVo();

        userRepository.save(user);
        return user.getId();
    }
    //-----------------로그인-----------------

    //-----------------조회-----------------
    public List<UserDetailDto> findAll() {
        return userRepository.findAll();
    }
    public UserDetailDto findById(Long id) {
        return userRepository.findDetailDtoById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
    public UserDetailDto findByEmail(String email) {
        return userRepository.findDetailDtoByEmail(email)
                .orElseThrow(EntityNotFoundException::new);
    }

    //-----------------수정-----------------


    //-----------------상태변경-----------------

    //-----------------기타-----------------
    public void validateDuplicateEmail(String email) {
        if(userRepository.existSameEmail(email)) throw new DuplicateEntityException("이메일이 중복됩니다.");
    }
}
