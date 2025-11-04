package com.example.muscle_market.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "reviews")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @Column(name = "review_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewr_user_idx", nullable = false)
    private User reviewer;

    // 리뷰를 받은 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_idx", nullable = false)
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_idx", nullable = false)
    private Transaction transaction;
}