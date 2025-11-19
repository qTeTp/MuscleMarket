package com.example.muscle_market.dto;

import com.example.muscle_market.enums.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingDto {
    private Long sportId;
    @NotBlank
    private String sportName;
    @NotNull
    private SkillLevel skillLevel;

}
