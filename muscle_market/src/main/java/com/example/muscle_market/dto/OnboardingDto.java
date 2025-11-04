package com.example.muscle_market.dto;

import com.example.muscle_market.enums.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OnboardingDto {

    @NotNull
    private String sportName;
    @NotNull
    private SkillLevel skillLevel;

}
