package com.ajh.flow.controller;

import com.ajh.flow.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public String userList(Model model){
        model.addAttribute("users",userService.findUsers());
        return "admin/userList";
    }
    @PostMapping("/user/disable/{id}")
    @ResponseBody
    public ResponseEntity<String> disableUser(@PathVariable("id") Long userId){
        userService.stopUser(userId);
        return ResponseEntity.ok("사용자의 계정이 비활성화 되었습니다.");
    }
    @PostMapping("/user/enable/{id}")
    @ResponseBody
    public ResponseEntity<String> enableUser(@PathVariable("id") Long userId){
        userService.resumeUser(userId);
        return ResponseEntity.ok("사용자의 계정이 활성화 되었습니다.");
    }
}
