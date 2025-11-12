package com.example.muscle_market.controller.api;

import com.example.muscle_market.domain.CustomUserDetails;
import com.example.muscle_market.dto.LoginDto;
import com.example.muscle_market.dto.LoginResponseDto;
import com.example.muscle_market.dto.PostUserDto;
import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setMaxAge(0);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
//        refreshCookie.setSecure(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("로그아웃 완료");
    }

    // 현재 로그인한 유저 정보 확인
    @GetMapping("/api/users/me")
    public ResponseEntity<PostUserDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails authUser) {
        PostUserDto curUser = userService.getCurrentUser(authUser.getId());
        return ResponseEntity.ok(curUser);
    }
}
