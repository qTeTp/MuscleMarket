package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SportDto {
    private Long id;
    private String name;
}