package com.example.muscle_market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateDto {
    private Long productId;
    private Long sellerId;
    private Long customerId;
    private String location;
    private Date transactionDate;
}
