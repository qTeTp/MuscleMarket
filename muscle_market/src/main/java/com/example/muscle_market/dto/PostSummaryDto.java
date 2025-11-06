package com.example.muscle_market.dto;

import java.time.LocalDateTime;

import com.example.muscle_market.domain.Post;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostSummaryDto {
    private Long postId;
    private String title;
    private String authorNickname;
    private String sportName;
    private Boolean isBungae;
    private Integer views;
    private LocalDateTime createdAt;

    public static PostSummaryDto fromEntity(Post post) {
        return PostSummaryDto.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .authorNickname(post.getAuthor().getNickname())
            .sportName(post.getSport().getName())
            .isBungae(post.getIsBungae())
            .views(post.getViews())
            .createdAt(post.getCreatedAt())
            .build();
    }
}
