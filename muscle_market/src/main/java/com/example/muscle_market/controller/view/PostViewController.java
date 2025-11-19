package com.example.muscle_market.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.muscle_market.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostViewController {
    private final PostService postService;

    // 전체 게시글
    @GetMapping
    public String getPostList() {
        return "post/postList";
    }

    // 게시글 상세
    @GetMapping("/{postId}")
    public String getPostDetail(@PathVariable Long postId) {
        return "post/postDetail";
    }

    // 게시글 생성
    @GetMapping("/new")
    public String getPostCreateView() {
        return "post/postCreate";
    }

    // 게시글 수정
    @GetMapping("/{postId}/edit")
    public String getPostUpdateView(@PathVariable Long postId) {
        if (postService.isBungae(postId)) return "post/bungaeUpdate";
        else return "post/postUpdate";
    }   

}
