package com.example.muscle_market.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "isBungae", nullable = false)
    private Boolean isBungae = false;

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bungae_id")
    private Bungae bungae;

    // TODO: 현재는 이미지를 본문 하단에 개수만큼 보여주도록 설계하지만, 
    //       시간이 된다면 본문 원하는 위치에 삽입할 수 있도록 설계를 고쳐야 함
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    @Builder
    public Post(String title, String content, User author, Sport sport, Bungae bungae) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.sport = sport;
        this.bungae = bungae;
        this.isBungae = bungae != null;
    }

    public void addImage(PostImage postImage) {
        this.postImages.add(postImage);
        postImage.setPost(this);
    }

    public void updatePost(String title, String content, Sport sport) {
        this.title = title;
        this.content = content;
        this.sport = sport;
    }
}
