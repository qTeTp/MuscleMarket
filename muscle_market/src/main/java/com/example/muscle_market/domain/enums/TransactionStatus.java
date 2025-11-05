package com.example.muscle_market.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    SELLING("판매중"),
    RESERVED("예약중"),
    SOLD("거래완료"),
    CANCELED("거래취소"),
    DELETE("삭제됨");

    private final String description;
}