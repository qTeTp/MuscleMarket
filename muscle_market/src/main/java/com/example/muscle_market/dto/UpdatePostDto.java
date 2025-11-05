package com.example.muscle_market.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePostDto {
    private String title;
    private String content;
    private String sportName;
    private List<String> postImages;

    @Builder
    public UpdatePostDto(String title, String content, String sportName, List<String> postImages) {
        this.title = title;
        this.content = content;
        this.sportName = sportName;
        this.postImages = postImages;
    }
}
