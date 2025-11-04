package com.example.muscle_market.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "sports")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sport {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="sport_idx")
    private Long id;

    @Column(name = "sport_name", length = 20, nullable = false)
    private String name;
}
