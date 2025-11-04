package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

// 구매 조회, 판매 조회 모두 사용
@Getter
@Builder
public class TransactionDto {
    private Long id;
    private String status; // 거래 상태
    private String location;
    private Date completedAt; // 거래 날짜

    private Long buyerId; // 구매자 id
    private String buyerName;
    private Long sellerId; // 판매자 id
    private String sellerName;

    private Long productId; // 상품 id, 명
    private String productName;
    private Float price;
}
