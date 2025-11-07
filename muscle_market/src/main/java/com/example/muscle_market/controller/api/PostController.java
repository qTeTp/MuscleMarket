package com.example.muscle_market.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.muscle_market.domain.CustomUserDetails;
import com.example.muscle_market.dto.CreatePostDto;
import com.example.muscle_market.dto.PostDetailDto;
import com.example.muscle_market.dto.PostSummaryDto;
import com.example.muscle_market.dto.UpdatePostDto;
import com.example.muscle_market.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<Page<PostSummaryDto>> getPosts(@PageableDefault(
        size = 10,
        sort = "createdAt",
        direction = Sort.Direction.DESC
    ) Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails authUser,
      @RequestParam(required = false) Long sportId,
      @RequestParam(required = false) Boolean isBungae,
      @RequestParam(required = false) String keyword
    ) {
        Page<PostSummaryDto> postPage = postService.getAllPosts(pageable, authUser.getId(), sportId, isBungae, keyword);
        return ResponseEntity.ok(postPage);
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailDto> getPostDetail(@AuthenticationPrincipal CustomUserDetails authUser, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId, authUser.getId()));
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<PostDetailDto> createPost(@AuthenticationPrincipal CustomUserDetails authUser, CreatePostDto request) {
        PostDetailDto createdPost = postService.createPost(authUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostDetailDto> updatePost(@AuthenticationPrincipal CustomUserDetails authUser, @PathVariable Long postId, UpdatePostDto request) {
        PostDetailDto updatedPost = postService.updatePost(authUser.getId(), postId, request);
        return ResponseEntity.ok(updatedPost);
    }

    // 조회수 증가
    @PutMapping("/{postId}/views")
    public ResponseEntity<Void> increaseView(@PathVariable Long postId) {
        postService.increaseView(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal CustomUserDetails authUser, @PathVariable Long postId) {
        postService.deletePost(postId, authUser.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
