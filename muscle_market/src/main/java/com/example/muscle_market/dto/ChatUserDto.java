package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatUserDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;

    @Builder
    public ChatUserDto(Long userId, String nickname, String profileImageUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
