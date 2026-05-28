package com.ajh.flow.controller;

import com.ajh.flow.common.exception.DuplicateEntityException;
import com.ajh.flow.domain.PrincipalDetails;
import com.ajh.flow.domain.User;
import com.ajh.flow.dto.user.UserLoginDto;
import com.ajh.flow.dto.user.UserRegisterDto;
import com.ajh.flow.dto.user.UserUpdateDto;
import com.ajh.flow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/home")
    public String home() {
        return "user/home";
    }
    //-----------------등록-----------------
    @GetMapping("/join")
    public String createForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "user/join";
    }
    @PostMapping("/join")
    public String create(@Valid UserRegisterDto dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "user/join";
        }
        userService.registerUser(dto);
        return "redirect:/user/login";
    }

    //-----------------조회-----------------
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("userLoginForm", new UserLoginDto());
        return "user/login";
    }

    @GetMapping("/detail")
    public String detail(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        model.addAttribute("user",principalDetails.getUser());
        return "user/detail";
    }

    //-----------------수정-----------------
    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        model.addAttribute("user",principalDetails.getUser());
        return "user/edit";
    }
    @PostMapping("/edit")
    public String edit(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid UserUpdateDto dto,
                       BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("user",principalDetails.getUser());
            return "user/edit";
        }
        User user = userService.editUserInfo(principalDetails.getUser().getId(),dto);

        principalDetails.updateUser(user);

        //현재 로그인된 인증 객체를 가져온다
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //null이 발생할 수 있는 credentials를 따로 처리
        //기존 인증 객체의 정보(권한)을 그대로 유지하면서, 정보가 수정된 principalDetails로 새 토큰을 만든다.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                principalDetails, //새 유저 정보가 반영된 객체
                auth != null ? auth.getCredentials() : null, //기존 비밀번호
                auth != null ? auth.getAuthorities() : null //기존 유저가 가진 권한 리스트
                );
        //시큐리티 메인 금고에 새 토큰을 집어넣는다.
        SecurityContextHolder.getContext().setAuthentication(token);


        return "redirect:/user/detail";
    }

    //-----------------상태변경-----------------
    //회원 탈퇴
    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.stopUser(principalDetails.getUser().getId());
        return "redirect:/user/logout";
    }
    //기타
    @ResponseBody
    @GetMapping("/api/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        try{
            userService.validateDuplicateEmail(email);
            res.put("available",true);
            res.put("message","사용 가능한 이메일 입니다.");
        }catch (DuplicateEntityException e){
            res.put("available",false);
            res.put("message","사용 불가능한 이메일 입니다.");
        }

        return ResponseEntity.ok(res);
    }
}
