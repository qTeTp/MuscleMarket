package com.example.muscle_market.controller.api;

import com.example.muscle_market.domain.User;
import com.example.muscle_market.repository.UserRepository;
import com.example.muscle_market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PostLoginController {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(PostLoginController.class);

    // 로그인 성공 후 호출
    @GetMapping("/post-login")
    public String postLoginRedirect(){
        // SecurityContext에서 로그인 유저 정보 가져오기
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            logger.warn("Authentication is null");
        } else {
            logger.info("Authentication 객체: {}", auth);
            logger.info("사용자 이름: {}", auth.getName());
            logger.info("인증 여부: {}", auth.isAuthenticated());
            logger.info("권한: {}", auth.getAuthorities());
        }

        String username = (auth != null) ? auth.getName() : null;

        if (username == null) {
            logger.error("username is null");
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 온보딩 확인
        if (!user.getIsOnboarded()) {
            // false이면 온보딩 페이지로 이동
            return "redirect:/onboarding";
        } else {
            // true면 메인페이지로 이동
            return "redirect:/products";
        }
    }
}
