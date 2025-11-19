package com.example.muscle_market.dto;

import com.example.muscle_market.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusUpdateDto {
    private String transactionId; // 거래 id
    private TransactionStatus status; // 변경 요청 상태
    private Long userId; // 요청 사용자
}
