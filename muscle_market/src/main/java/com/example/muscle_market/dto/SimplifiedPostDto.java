package com.example.muscle_market.dto;

import java.time.LocalDateTime;

import com.example.muscle_market.domain.Post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SimplifiedPostDto {
    private Long postId;
    private String title;
    private String authorNickname;
    private LocalDateTime createdAt;

    public SimplifiedPostDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.authorNickname = post.getAuthor().getNickname();
        this.createdAt = post.getCreatedAt();
    }
}
