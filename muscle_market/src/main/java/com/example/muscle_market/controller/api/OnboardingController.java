package com.example.muscle_market.controller.api;

import com.example.muscle_market.domain.CustomUserDetails;
import com.example.muscle_market.dto.OnboardingDto;
import com.example.muscle_market.dto.UserFavoriteSportDto;
import com.example.muscle_market.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OnboardingController {
    private final OnboardingService onboardingService;

    // 온보딩 입력
    @PostMapping("/onboarding")
    public ResponseEntity<String> onboarding(
            @Valid @RequestBody OnboardingDto onboardingDto) {
        String msg = onboardingService.completeOnboarding(onboardingDto);
        return ResponseEntity.ok(msg);
    }

    // 온보딩 수정
    @PutMapping("/favorite-sport")
    public ResponseEntity<String> updateFavoriteSport(
            @Valid @RequestBody OnboardingDto dto) {
        String msg = onboardingService.updateFavoriteSport(dto);
        return ResponseEntity.ok(msg);
    }

    // 유저id를 통해 사용자의 선호 운동 반환
    @GetMapping("/favorite-sport")
    public ResponseEntity<UserFavoriteSportDto> getOnboarding(
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getId();
        try {
            UserFavoriteSportDto dto = onboardingService.getFavoriteSport();
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
