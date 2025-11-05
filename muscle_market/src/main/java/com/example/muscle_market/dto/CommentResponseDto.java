package com.example.muscle_market.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private PostUserDto author;
    private Long parentCommentId;

    @Builder
    public CommentResponseDto(Long commentId, String content, LocalDateTime createdAt, PostUserDto author, Long parentCommentId) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.author = author;
        this.parentCommentId = parentCommentId;
    }
}
