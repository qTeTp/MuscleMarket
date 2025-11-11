package com.example.muscle_market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    private String title;
    private String description;
    private Long price;
    private String location;
    private Long sportId; // 종목 ID
    private Long authorId; // 작성자 ID
}
