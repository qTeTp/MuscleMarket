package com.example.muscle_market.controller.view;

import com.example.muscle_market.dto.OnboardingDto;
import com.example.muscle_market.dto.UserFavoriteSportDto;
import com.example.muscle_market.enums.SkillLevel;
import com.example.muscle_market.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class OnboardingPageController {
    private final OnboardingService onboardingService;

    @GetMapping("/onboarding")
    public String onboardingPage(Model model) {
        model.addAttribute("onboardingDto", new OnboardingDto());
        model.addAttribute("isEditMode", false);
        return "onboarding";
    }

    @GetMapping("/edit_onboarding")
    public String onboardingEdit(Model model) {
        try {
            // 로그인된 사용자의 선호 운동 정보 조회
            UserFavoriteSportDto favoriteSport = onboardingService.getFavoriteSport();

            // dto에 담기
            OnboardingDto dto = new OnboardingDto();
            dto.setSportName(favoriteSport.getSportName());
            dto.setSkillLevel(SkillLevel.valueOf(favoriteSport.getSkillLevel()));

            System.out.println("선호 운동, 레벨: " + favoriteSport.getSportName() + ", " + SkillLevel.valueOf(favoriteSport.getSkillLevel()));
            model.addAttribute("onboardingDto", dto);
            model.addAttribute("isEditMode", true); // 모드 추가 프론트 변환을 위해

        } catch (RuntimeException e) {
            model.addAttribute("onboardingDto", new OnboardingDto());
            model.addAttribute("isEditMode", true);
            model.addAttribute("errorMessage", "선호 운동 정보가 없습니다.");
        }
        return "onboarding";
    }
}
