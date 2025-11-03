package com.example.muscle_market.dto;

import java.util.List;
import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailDto {
    private Long id;
    private String title;
    private String description;
    private Float price;
    private String location;
    private Date createdAt;
    private Date updatedAt;
    private Long views;

    private String authorName; // 작성자 닉네임
    private String sportName; // 운동
    private List<String> imageUrls; // 이미지 URL 리스트
    private Long likeCount;
}
