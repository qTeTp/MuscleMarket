package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.LoginDto;
import com.example.muscle_market.dto.LoginResponseDto;
import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody UserDto userDto){
        userService.singUp(userDto);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto); // JWT 토큰들 반환 (access token, refresh token)
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal String username){
        userService.logout();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
