package com.example.muscle_market.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.authentication.AuthenticationConverter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 컨텐츠들 접근 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        // 인증 없어도 들어가게
                        .requestMatchers("/", "/api/signup", "/api/login", "/api/**", "/api/products",
                                "/api/products/**", "/api/users/{userId}/likes",
                                "/login", "/signup", "/images/**", "/products/**", "/products/new", "/post-login", "/api/sports",
                                "/api/sports/**", "/products/detail/**", "/products", "/products/**").permitAll() // 회원가입/로그인은 허용
                        // 인증 있어야 들어가게
                        .requestMatchers("/ws-stomp", "/pub/**", "/sub/**").authenticated()
                        .anyRequest().authenticated()
                )
                // JWT 필터 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );
        // 프레임 옵션 비활성화
        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
