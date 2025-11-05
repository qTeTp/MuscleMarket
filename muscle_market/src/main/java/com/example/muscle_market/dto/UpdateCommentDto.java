package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateCommentDto {
    private String content;

    @Builder
    public UpdateCommentDto(String content) {
        this.content = content;
    }
}
