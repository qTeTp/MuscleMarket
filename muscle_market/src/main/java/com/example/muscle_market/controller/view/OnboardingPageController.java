package com.example.muscle_market.controller.view;

import com.example.muscle_market.dto.OnboardingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class OnboardingPageController {

    @GetMapping("/onboarding")
    public String onboardingPage(Model model) {
        model.addAttribute("onboardingDto", new OnboardingDto());
        return "onboarding";
    }
}
