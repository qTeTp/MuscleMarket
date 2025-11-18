package com.example.muscle_market.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.muscle_market.domain.CustomUserDetails;
import com.example.muscle_market.dto.CommentResponseDto;
import com.example.muscle_market.dto.CreateCommentDto;
import com.example.muscle_market.dto.UpdateCommentDto;
import com.example.muscle_market.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class CommentController {
    private final CommentService commentService;

    // 게시글 댓글 전체 조회
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> getAllComments(@PathVariable Long postId, Pageable pageable) {
        Page<CommentResponseDto> commentPage = commentService.getAllComments(postId, pageable);
        return ResponseEntity.ok(commentPage);
    }

    // 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails authUser, @RequestBody CreateCommentDto request) {
        CommentResponseDto createdComment = commentService.createComment(postId, authUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    // 댓글 수정
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails authUser, @RequestBody UpdateCommentDto request) {
        CommentResponseDto updatedComment = commentService.updateComment(postId, commentId, authUser.getId(), request);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails authUser) {
        commentService.deleteComment(postId, commentId, authUser.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
