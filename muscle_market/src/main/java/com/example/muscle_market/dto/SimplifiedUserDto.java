package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SimplifiedUserDto {
    private Long userId;
    private String username;
    private String nickname;
    private String profileImgUrl;

    @Builder
    public SimplifiedUserDto(Long userId, String username, String nickname, String profileImgUrl) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
    }
}
