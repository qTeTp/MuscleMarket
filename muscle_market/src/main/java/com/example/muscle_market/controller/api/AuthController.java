package com.example.muscle_market.controller.api;

import com.example.muscle_market.domain.CustomUserDetails;
import com.example.muscle_market.dto.LoginDto;
import com.example.muscle_market.dto.LoginResponseDto;
import com.example.muscle_market.dto.SimplifiedUserDto;
import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
            // 성공하면 로그인페이지로 리다이렉트
            return "redirect:/login";
        } catch (RuntimeException e) {
            // 실패 시 alert 창으로 메시지 띄우기
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/api/login")
    public String login(@ModelAttribute("loginDto") LoginDto loginDto,
                        HttpServletResponse response,
                        Model model) {
        try {
            userService.login(loginDto, response); // JWT 토큰들 저장
            return "redirect:/post-login";    // 성공하면 온보딩 여부 확인 api로
        } catch (Exception e) {
            model.addAttribute("error", "로그인 실패 : " + e.getMessage());
            return "login";
        }
    }

    // 로그아웃
    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        userService.logout();

        // accessToken, refreshToken 쿠키 제거
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict") // 반드시 같은 사이트에서만 전송
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());


        return ResponseEntity.ok("로그아웃 완료");
    }

    // 현재 로그인한 유저 정보 확인
    @GetMapping("/api/users/me")
    public ResponseEntity<SimplifiedUserDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails authUser) {
        SimplifiedUserDto curUser = userService.getCurrentUser(authUser.getId());
        return ResponseEntity.ok(curUser);
    }
}
