package com.example.muscle_market.controller.view;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인된 상태면 poducts 페이지로 리다이렉트
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/products";
        }

        return "redirect:/login";
    }
}
