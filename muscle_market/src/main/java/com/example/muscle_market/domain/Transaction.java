package com.example.muscle_market.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Entity
@Table(name = "transactions")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {
    @Id
    @Column(name="transaction_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer status; // 거래 상태

    @Column
    private String location;

    @Column(name = "date")
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_idx")
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_idx")
    private User user2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_idx")
    private Product product;
}
