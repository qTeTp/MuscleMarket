package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCommentDto {
    private Long parentCommentId;
    private String content;

    @Builder
    public CreateCommentDto(Long parentCommentId, String content) {
        this.parentCommentId = parentCommentId;
        this.content = content;
    }
}
