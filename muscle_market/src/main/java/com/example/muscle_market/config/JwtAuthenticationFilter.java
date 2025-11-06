package com.example.muscle_market.config;

import com.example.muscle_market.domain.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 필터 정상작동하는지 확인하기 위한 print
        System.out.println("필터 실행 : " + request.getRequestURI());

        String token = null;

        // 헤더에서 Bearer 토큰 확인
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
            // 쿠키에서 accessToken 확인
            if(token == null && request.getCookies() != null) {
                Optional<Cookie> accessCookie = Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals("accessToken"))
                        .findFirst();
                if (accessCookie.isPresent()) {
                    token = accessCookie.get().getValue();
                }
            }

            // 토큰이 있으면 검증 후 SecurityContext 세팅
            if (token != null && jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);

                CustomUserDetails userDetails =
                        (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("SecurityContext에 인증 정보 설정됨 : " + username);



//            if (jwtUtil.validateToken(token)) {
//                String username = jwtUtil.extractUsername(token);
//
//                CustomUserDetails userDetails =
//                        (CustomUserDetails) userDetailsService.loadUserByUsername(username);
//
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
        }
        filterChain.doFilter(request, response);
    }
}
