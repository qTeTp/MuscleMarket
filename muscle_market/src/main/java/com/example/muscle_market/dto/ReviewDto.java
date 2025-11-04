package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ReviewDto {
    private Long id;
    private Long transactionId; // 거래 내역. 한 거래에 최대 두개의 리뷰가 존재 가능
    private String content;
    private Date createdAt;
    private Long authorId; // 작성자
    private String authorName;
    private Long targetId; // 리뷰 받는 사람
    private String targetName;
}
