package com.example.muscle_market.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "product_likes")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLike {
    @Id
    @Column(name="product_likes_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_idx")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;
}
