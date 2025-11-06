package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostUserDto {
    private Long authorId;
    private String authorUsername;
    private String authorNickname;
    private String authorProfileImgUrl;

    @Builder
    public PostUserDto(Long authorId, String authorUsername, String authorNickname, String authorProfileImgUrl) {
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.authorNickname = authorNickname;
        this.authorProfileImgUrl = authorProfileImgUrl;
    }
}
