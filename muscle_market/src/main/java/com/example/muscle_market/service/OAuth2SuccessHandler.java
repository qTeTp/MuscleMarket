package com.example.muscle_market.service;

import com.example.muscle_market.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 토큰 발급
        String[] tokens = userService.oauthLogin(email, name);
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        // 쿠키 세팅
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .path("/")
                .secure(true) // HTTPS 연결에서만 전송
                .sameSite("Strict")   // 다른 사이트에서 요청시 쿠키 자동전송 방지
                .maxAge(60*15)  // 15분
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .secure(true) // HTTPS 연결에서만 전송
                .sameSite("Strict")   // 다른 사이트에서 요칭시 쿠키 자동전송 방지
                .maxAge(60 * 60 * 24 * 7)   // 7일
                .build();

        response.addHeader("set-Cookie", accessCookie.toString());
        response.addHeader("set-Cookie", refreshCookie.toString());

        // DB에서 유저 조회 (온보딩 상태 확인)
        User user = userService.getUserByEmail(email);

        if (user.getIsOnboarded() != null && !user.getIsOnboarded()) {
            response.sendRedirect("/onboarding");   // 온보딩 필요
        } else {
            response.sendRedirect("/products");
        }
    }
}
