package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimplifiedUserDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;

    @Builder
    public SimplifiedUserDto(Long userId, String nickname, String profileImageUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
