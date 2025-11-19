package com.example.muscle_market.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserFavoriteSportDto {
    private String sportName;
    private String skillLevel;
}