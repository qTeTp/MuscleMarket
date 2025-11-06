package com.example.muscle_market.controller.view;

import com.example.muscle_market.dto.LoginDto;
import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "signup";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }
}
