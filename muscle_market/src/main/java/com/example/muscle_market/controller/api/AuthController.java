package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.LoginDto;
import com.example.muscle_market.dto.LoginResponseDto;
import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public String signup(@ModelAttribute("userDto") UserDto userDto, Model model) {
        try {
            userService.singUp(userDto);
            // 성공하면 온보딩 확인 api로
            return "redirect:/post-login";
        } catch (RuntimeException e) {
            // 실패 시 alert 창으로 메시지 띄우기
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/api/login")
    public String login(@ModelAttribute("loginDto") LoginDto loginDto, Model model) {
        try {
            LoginResponseDto response = userService.login(loginDto); // JWT 토큰들 저장
            return "redirect:/products/list";    // 메인페이지로 인데 메인페이지가 아직없음
        } catch (Exception e) {
            model.addAttribute("error", "로그인 실패 : " + e.getMessage());
            return "login";
        }
    }

    // 로그아웃
    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal String username){
        userService.logout();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
