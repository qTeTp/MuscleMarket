package com.example.muscle_market.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private PostUserDto author;
    private PostUserDto targetAuthor;
    private boolean isDeleted;
    private boolean isReply;

    public static CommentResponseDto fromEntity(Comment comment) {
        return new CommentResponseDto(comment);
    }

    // 부모 dto 생성자
    private CommentResponseDto(Comment entity) {
        this.commentId = entity.getCommentId();
        this.createdAt = entity.getCreatedAt();
        this.isReply = entity.getParent() != null;
        this.author = PostUserDto.builder()
            .authorId(entity.getAuthor().getId())
            .authorUsername(entity.getAuthor().getUsername())
            .authorNickname(entity.getAuthor().getNickname())
            .authorProfileImgUrl(entity.getAuthor().getProfileImgUrl())
            .build();

        // targetAuthor 설정
        if (entity.getParent() != null) {
            User parentAuthor = entity.getParent().getAuthor();
            this.targetAuthor = PostUserDto.builder()
                .authorId(parentAuthor.getId())
                .authorUsername(parentAuthor.getUsername())
                .authorNickname(parentAuthor.getNickname())
                .authorProfileImgUrl(parentAuthor.getProfileImgUrl())
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
