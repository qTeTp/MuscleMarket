package com.example.muscle_market.config;

import com.example.muscle_market.service.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // 소셜 로그인
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // 순환참조로 인해 Lazy 어노테이션 추가
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Lazy OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 활성화 (쿠키에 저장)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/logout")
                )

                // patch 적용시키기 위해 필요함
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String uri = request.getRequestURI();

                            // API 호출이면 JSON 반환
                            if (uri.startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                            } else {
                                // HTML 요청이면 로그인 페이지로 리다이렉트
                                response.sendRedirect("/login?redirect=" + uri);
                            }
                        })
                )
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 컨텐츠들 접근 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
//                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").permitAll()
//                        .requestMatchers("/products").permitAll()
                        .requestMatchers("/", "/login","/signup","/api/signup","/api/login","/api/logout").permitAll()
                        // 인증 없어도 들어가게
//                        .requestMatchers("/api/**", "/api/products",
//                                "/api/products/**", "/api/users/{userId}/likes", "/api/products/{productId}/like",
//                                "/login", "/signup", "/images/**", "/products/**", "/products/new", "/post-login", "/api/sports",
//                                "/api/sports/**", "/products/detail/**", "/products", "/products/**", "/post-login", "/oauth2/**",
//                                "/api/map/**", "/api/alan/chat", "/products/my", "/products/my/**").permitAll() // 회원가입/로그인은 허용
                        //  나머지는 인증 있어야 들어가게
                        .requestMatchers("/ws-stomp/**", "/pub/**", "/sub/**").authenticated()
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oAuth2SuccessHandler))

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
    public AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
