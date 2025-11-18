package com.example.muscle_market.dto;

import java.util.List;
import java.util.Date;

import com.example.muscle_market.enums.TransactionStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailDto {
    private Long id;
    private String title;
    private String description;
    private Long price;
    private String location;
    private List<String> productImageUrls; // 이미지 URL 리스트
    private List<Long> productImageIds; // 이미지 id 리스트
    private TransactionStatus status;

    private Date createdAt;
    private Date updatedAt;
    private Long views;

    // dto에서 작정자 정보 추출
    private UserDto user;
    private String sportName; // 운동

    private Long likeCount;
    private boolean isLiked; // 현재 사용자가 찜했나?
}
