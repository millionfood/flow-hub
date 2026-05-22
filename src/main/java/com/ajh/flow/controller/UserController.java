package com.ajh.flow.controller;

import com.ajh.flow.common.exception.DuplicateEntityException;
import com.ajh.flow.domain.PrincipalDetails;
import com.ajh.flow.dto.user.UserDetailDto;
import com.ajh.flow.dto.user.UserLoginDto;
import com.ajh.flow.dto.user.UserRegisterDto;
import com.ajh.flow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

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

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("userList",userService.findAll());
        return "user/list";
    }
    @GetMapping("/detail")
    public String detail(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        model.addAttribute("user",principalDetails.getUser());
        return "user/detail";
    }

    //-----------------수정-----------------


    //-----------------상태변경-----------------

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
