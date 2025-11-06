package com.example.muscle_market.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePostDto {
    private String title;
    private String content;
    private String sportName;
    private Long bungaeId;
    private List<String> postImages;

    @Builder
    public CreatePostDto(String title, String content, String sportName, Long bungaeId, List<String> postImages) {
        this.title = title;
        this.content = content;
        this.sportName = sportName;
        this.bungaeId = bungaeId;
        this.postImages = postImages;
    }
}
