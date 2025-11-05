package com.example.muscle_market.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSummaryDto {
    private Long postId;
    private String title;
    private String authorNickname;
    private String sportName;
    private Integer isBungae;
    private Integer views;
    private LocalDateTime createdAt;

    @Builder
    public PostSummaryDto(Long postId, String title, String authorNickname, String sportName, Integer isBungae, Integer views, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.authorNickname = authorNickname;
        this.sportName = sportName;
        this.isBungae = isBungae;
        this.views = views;
        this.createdAt = createdAt;
    }
}
