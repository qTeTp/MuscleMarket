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

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "signup";
    }

    @PostMapping("/api/signup")
    public String signup(@ModelAttribute("userDto") UserDto userDto, Model model) {
        try {
            userService.singUp(userDto);
            return "redirect:/login";
        } catch (RuntimeException e) {
            // 실패 시 alert 창으로 메시지 띄우기
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
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
