package com.example.muscle_market.dto;

import com.example.muscle_market.domain.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlanChatResponse {
    private String alanAnswer;
    private List<ProductSimpleResponse> recommendProducts;
}
