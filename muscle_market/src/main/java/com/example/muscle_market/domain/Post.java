package com.example.muscle_market.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
@Getter
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Setter
    @Column(name = "post_title", nullable = false)
    private String title;

    @Setter
    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_bungae", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer isBungae;

    @Column(name = "views", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bungae_id")
    private Bungae bungae;

    @Builder
    public Post(String title, String content, User author, Sport sport, Bungae bungae) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.sport = sport;
        this.bungae = bungae;

        if (bungae == null) this.isBungae = 0;
        else this.isBungae = 1;
    }
}
