package com.example.muscle_market.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "product_images")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {
    @Id
    @Column(name="product_image_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_idx")
    private Product product;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String s3Key;  // S3에서의 파일 키

    @Column(nullable = false)
    private String s3Url;  // S3 파일 URL

    @Column(nullable = false)
    private String originalFilename; // 파일명
}
