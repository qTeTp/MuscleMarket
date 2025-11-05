package com.example.muscle_market.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePostDto {
    private String title;
    private String content;
    private String sportName;
    private Integer isBungae;
    private List<String> postImages;

    @Builder
    public CreatePostDto(String title, String content, String sportName, Integer isBungae, List<String> postImages) {
        this.title = title;
        this.content = content;
        this.sportName = sportName;
        this.isBungae = isBungae;
        this.postImages = postImages;
    }
}
