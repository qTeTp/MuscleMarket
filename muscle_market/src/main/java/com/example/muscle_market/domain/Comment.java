package com.example.muscle_market.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "comments")
@Getter
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE comments SET deleted_at = CURRENT_TIMESTAMP WHERE comment_id = ?")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "comment_content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 댓글과 대댓글 그룹화를 위해 사용됨
    @Column(name = "root_id")
    @Setter
    private Long rootId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> children = new ArrayList<>();

    private void setParent(Comment parentComment) {
        this.parent = parentComment;
        parentComment.children.add(this);
    }

    public static Comment createParentComment(String content, Post post, User author) {
        Comment comment = new Comment();
        comment.content = content;
        comment.author = author;
        comment.post = post;
        comment.parent = null;
        return comment;
    }

    public static Comment createChildComment(String content, Post post, User author, Comment parentComment) {
        Comment comment = new Comment();
        comment.content = content;
        comment.author = author;
        comment.post = post;
        comment.rootId = parentComment.getRootId();
        comment.setParent(parentComment);
        return comment;
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
