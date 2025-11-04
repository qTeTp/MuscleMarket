package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ProductListDto {
    private Long id;
    private String title;

    private String description;
    private Float price;
    private String sportName;
    private String location;

    private Long views;

    private Long likeCount; // Service에서 계산
    private String thumbnailUrl; // 썸네일 이미지 URL (id가 가장 낮은 이미지)

    private Date createdAt;
    private Date updatedAt;
}
