package com.example.muscle_market.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.muscle_market.dto.BungaeDetails;
import com.example.muscle_market.enums.BungaeStatus;
import com.example.muscle_market.enums.PostStatus;
import com.example.muscle_market.exception.InvalidRequestException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
@Getter
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE posts SET post_status = 'DELETED' WHERE post_id = ?")
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

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Column(name = "post_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus = PostStatus.ACTIVE;

    // 번개 관련 게시글에 쓰는 필드
    @Column(name = "is_bungae", nullable = false)
    private Boolean isBungae = false;

    @Column(name = "bungae_location")
    private String bungaeLocation;

    @Column(name = "bungae_max_participants")
    private Integer maxParticipants = 0;

    @Column(name = "bungae_cur_participants")
    private Integer curParticipants = 0;

    @Column(name = "bungae_datetime")
    private String bungaeDatetime;

    @Column(name = "bungae_status")
    @Enumerated(EnumType.STRING)
    private BungaeStatus bungaeStatus = BungaeStatus.OPENED;

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

    // TODO: 현재는 이미지를 본문 하단에 개수만큼 보여주도록 설계하지만, 
    //       시간이 된다면 본문 원하는 위치에 삽입할 수 있도록 설계를 고쳐야 함
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    // 일반 글 생성용 팩토리 메서드
    public static Post createNormalPost(String title, String content, User author, Sport sport) {
        Post post = new Post();
        post.title = title;
        post.content = content;
        post.author = author;
        post.sport = sport;
        post.isBungae = false;
        return post;
    }

    // 번개 글 생성용 팩토리 메서드
    public static Post createBungaePost(String title, String content, User author, Sport sport, BungaeDetails details) {
        Post post = new Post();
        post.title = title;
        // 혹시라도 기타 사항을 안적었으면 빈 칸으로라도 저장
        post.content = content == null || content.isEmpty() ? "" : content;
        post.author = author;
        post.sport = sport;
        post.isBungae = true;
        post.bungaeLocation = details.bungaeLocation();
        post.maxParticipants = details.maxParticipants();
        post.curParticipants = details.curParticipants();
        post.bungaeDatetime = details.bungaeDatetime();
        post.bungaeStatus = details.bungaeStatus();
        return post;
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

    public void updateBungae(String title, String content, Sport sport, BungaeDetails details) {
        this.title = title;
        // 혹시라도 기타 사항을 안적었으면 빈 칸으로라도 저장
        this.content = content == null || content.isEmpty() ? "" : content;
        this.sport = sport;
        this.bungaeLocation = details.bungaeLocation();
        this.maxParticipants = details.maxParticipants();
        this.curParticipants = details.curParticipants();
        this.bungaeDatetime = details.bungaeDatetime();
        this.bungaeStatus = details.bungaeStatus();
    }

    public void hidePost() {
        if (this.postStatus == PostStatus.ACTIVE) {
            this.postStatus = PostStatus.HIDDEN;
        } else throw new InvalidRequestException("이미 처리되었거나 숨길 수 없는 상태입니다.");
    }

    public void unhidePost() {
        if (this.postStatus == PostStatus.HIDDEN) {
            this.postStatus = PostStatus.ACTIVE;
        } else throw new InvalidRequestException("숨김 해제할 수 없는 상태입니다.");
    }
}
