package com.example.muscle_market.domain;

import com.example.muscle_market.enums.SkillLevel;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_favorite_sports",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_idx"})   // 유저당 1개만 저장
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserFavoriteSport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_favorite_sports_idx")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillLevel skillLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_idx", nullable = false)
    private Sport sport;

}
