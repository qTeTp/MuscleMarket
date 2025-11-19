package com.example.muscle_market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 물품 데이터 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {
    private String title;
    private String description;
    private Long price;
    private String location;
    private Long sportId;
    private String status;
    // 이미지 리스트
    private List<Long> deletedImageIds;
}
