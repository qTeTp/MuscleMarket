package com.example.muscle_market.config;

import com.example.muscle_market.domain.CustomUserDetails;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("필터 실행 : " + request.getRequestURI());

        String accessToken = null;
        String refreshToken = null;

        // 1️⃣ 쿠키에서 AccessToken, RefreshToken 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        try {
            // AccessToken 유효 → 그대로 인증
            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                setAuthentication(jwtUtil.extractUsername(accessToken));
            }

        } catch (ExpiredJwtException e) {
            // AccessToken 만료 → RefreshToken 검사
            System.out.println("AccessToken 만료됨, RefreshToken 확인 중...");

            if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                String username = jwtUtil.extractUsername(refreshToken);

                // 4️⃣ DB에 저장된 hashed RefreshToken과 일치하는지 확인
                User user = userRepository.findByUsername(username).orElse(null);
                if (user != null && user.getRefreshToken() != null) {
                    String hashedRefresh = hashToken(refreshToken);
                    if (hashedRefresh.equals(user.getRefreshToken())) {
                        // 일치 → AccessToken 재발급
                        String newAccessToken = jwtUtil.generateToken(username);
                        ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
                                .httpOnly(true)
                                .path("/")
                                .maxAge(60 * 15)
                                .build();

                        response.addHeader("Set-Cookie", newAccessCookie.toString());
                        System.out.println("새 AccessToken 재발급 완료 : " + username);

                        setAuthentication(username);
                    }
                }
            } else {
                System.out.println("RefreshToken 만료 또는 없음 → 재로그인 필요");
            }
        }

        filterChain.doFilter(request, response);
    }

    // SHA-256 해시 함수
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("토큰 해싱 실패", e);
        }
    }

    // SecurityContext에 인증 정보 설정
    private void setAuthentication(String username) {
        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
