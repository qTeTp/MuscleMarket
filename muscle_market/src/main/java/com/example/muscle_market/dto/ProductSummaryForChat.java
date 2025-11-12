package com.example.muscle_market.dto;

import com.example.muscle_market.enums.TransactionStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductSummaryForChat {
    private Long productId;
    private String title;
    private Long price;
    private TransactionStatus status;
    private String thumbnail;

    @Builder
    public ProductSummaryForChat(Long productId, String title, Long price, TransactionStatus status, String thumbnail) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.status = status;
        this.thumbnail = thumbnail;
    }
}
