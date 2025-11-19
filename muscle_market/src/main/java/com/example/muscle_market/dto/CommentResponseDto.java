package com.example.muscle_market.dto;

import java.time.LocalDateTime;

import com.example.muscle_market.domain.Comment;
import com.example.muscle_market.domain.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private SimplifiedUserDto author;
    private SimplifiedUserDto targetAuthor;
    private boolean isDeleted;
    private boolean isReply;
    private Long parentId;

    public static CommentResponseDto fromEntity(Comment comment) {
        return new CommentResponseDto(comment);
    }

    // 부모 dto 생성자
    private CommentResponseDto(Comment entity) {
        this.commentId = entity.getCommentId();
        this.createdAt = entity.getCreatedAt();
        this.isReply = entity.getParent() != null;
        if (this.isReply == true) {
            this.parentId = entity.getParent().getCommentId();
        } else {
            this.parentId = null;
        }

        this.author = SimplifiedUserDto.builder()
            .userId(entity.getAuthor().getId())
            .username(entity.getAuthor().getUsername())
            .nickname(entity.getAuthor().getNickname())
            .profileImgUrl(entity.getAuthor().getProfileImgUrl())
            .build();

        // targetAuthor 설정
        if (entity.getParent() != null) {
            User parentAuthor = entity.getParent().getAuthor();
            this.targetAuthor = SimplifiedUserDto.builder()
                .userId(parentAuthor.getId())
                .username(parentAuthor.getUsername())
                .nickname(parentAuthor.getNickname())
                .profileImgUrl(parentAuthor.getProfileImgUrl())
                .build();
        } else {
            this.targetAuthor = null;
        }
        
        if (entity.getDeletedAt() != null) {
            // 삭제된 댓글이라면 API 결과물의 댓글 내용은 '삭제된 댓글입니다.'로 두기
            this.isDeleted = true;
            this.content = "삭제된 댓글입니다.";
        } else {
            this.isDeleted = false;
            this.content = entity.getContent();
        }
    }
}
