package com.example.muscle_market.domain;

import com.example.muscle_market.enums.TransactionStatus;
import com.example.muscle_market.dto.ProductUpdateDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "product_post_idx")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column
    private Float price;

    @Column
    private String location;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column
    private Long views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_idx")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_idx")
    private Sport sport;

    // 거래 상태 추가
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    // 조회수 증가
    public void setViews() {
        this.views++;
    }

    // 물품 업데이트
    public void updateProduct(ProductUpdateDto request, Sport sport) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.price = request.getPrice();
        this.location = request.getLocation();
        this.sport = sport;
        this.updatedAt = new Date();
    }

    // 상품 상태 변경
    // 판매 중, 예약 중, 판매 완료, 물품 삭제
    public void updateStatus(TransactionStatus newStatus) {
        this.status = newStatus;
        // 예약 중, 판매 완료, 물품 삭제는 채팅 금지 로직 추가
    }

    // default값 설정
    @Builder
    public Product(Long id, String title, String description, Float price, String location, Date createdAt, Date updatedAt, Long views, User user, Sport sport, TransactionStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.views = views;
        this.user = user;
        this.sport = sport;
        this.status = (status != null) ? status : TransactionStatus.SELLING; // 기본값 판매중
    }
}
