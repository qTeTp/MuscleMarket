package com.example.muscle_market.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDetailDto {
    private Long postId;
    private String title;
    private String content;
    private String sportName;
    private Integer isBungae;
    private Long bungaeId;
    private LocalDateTime createdAt;
    private PostUserDto postAuthor;
    private List<String> postImages;

    @Builder
    public PostDetailDto(Long postId, String title, String content, String sportName,
            Integer isBungae, Long bungaeId, LocalDateTime createdAt, PostUserDto postAuthor, List<String> postImages) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.sportName = sportName;
        this.isBungae = isBungae;
        this.bungaeId = bungaeId;
        this.createdAt = createdAt;
        this.postAuthor = postAuthor;
        this.postImages = postImages;
    }
}
