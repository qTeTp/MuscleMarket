package com.example.muscle_market.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.muscle_market.domain.Comment;
import com.example.muscle_market.domain.Post;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.dto.CommentResponseDto;
import com.example.muscle_market.dto.CreateCommentDto;
import com.example.muscle_market.dto.UpdateCommentDto;
import com.example.muscle_market.enums.PostStatus;
import com.example.muscle_market.exception.InvalidRequestException;
import com.example.muscle_market.repository.CommentRepository;
import com.example.muscle_market.repository.CommentSpecification;
import com.example.muscle_market.repository.PostRepository;
import com.example.muscle_market.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 작성자-로그인유저 검증
    private void validateUser(Long authorId, Long userId) {
        if(!authorId.equals(userId)) {
            throw new AccessDeniedException("사용자와 작성자가 일치하지 않습니다.");
        }
    }

    // 게시글 댓글 조회
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getAllComments(Long postId, Pageable pageable) {
        // 먼저 일반 댓글이 시간 순 정렬이긴 하나, 대댓글이 있다면 대댓글이 다른 댓글보다 먼저 와야 함
        Sort sort = Sort.by(
            Sort.Order.asc("rootId"),
            Sort.Order.asc("createdAt")
        );

        // 페이지와 크기에 맞춰 서버 정렬을 조합
        Pageable serverPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            sort
        );

        // Specification을 이용해 원하는 댓글들 가져오기
        Specification<Comment> spec = CommentSpecification.getAllComments(postId);
        Page<Comment> commentPage = commentRepository.findAll(spec, serverPageable);

        return commentPage.map(CommentResponseDto::fromEntity);
    }

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long postId, Long authorId, CreateCommentDto request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));
        
        // 유저 검증
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 게시글 상태 검증 (삭제에 댓글 비허용, 숨김 글에는 본인만 댓글 허용)
        if (post.getPostStatus() == PostStatus.DELETED) {
            throw new InvalidRequestException("삭제된 게시글에는 댓글을 작성하실 수 없습니다");
        } else if (post.getPostStatus() == PostStatus.HIDDEN) {
            validateUser(post.getAuthor().getId(), authorId);
        }

        Comment comment;
        if (request.getParentCommentId() == null) {
            // 일반 댓글일 때:
            comment = Comment.createParentComment(request.getContent(), post, author);
            commentRepository.saveAndFlush(comment);
            comment.setRootId(comment.getCommentId());
        } else {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new EntityNotFoundException("대댓글을 달 대상을 찾을 수 없습니다."));
            comment = Comment.createChildComment(request.getContent(), post, author, parent);
            commentRepository.save(comment);
        }

        return CommentResponseDto.fromEntity(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long postId, Long commentId, Long authorId, UpdateCommentDto request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));
        
        // 유저 검증
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 게시글 상태 검증 (삭제에 댓글 비허용, 숨김 글에는 본인만 댓글 허용)
        if (post.getPostStatus() == PostStatus.DELETED) {
            throw new InvalidRequestException("삭제된 게시글에는 댓글을 작성하실 수 없습니다");
        } else if (post.getPostStatus() == PostStatus.HIDDEN) {
            validateUser(post.getAuthor().getId(), authorId);
        }

        // 댓글 검증
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        
        // 댓글 작성자 검증
        validateUser(comment.getAuthor().getId(), authorId);

        comment.updateComment(request.getContent());
        return CommentResponseDto.fromEntity(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long postId, Long commentId, Long authorId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));
        
        // 유저 검증
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 게시글 상태 검증 (삭제에 댓글 비허용, 숨김 글에는 본인만 댓글 허용)
        if (post.getPostStatus() == PostStatus.DELETED) {
            throw new InvalidRequestException("삭제된 게시글에는 댓글을 작성하실 수 없습니다");
        } else if (post.getPostStatus() == PostStatus.HIDDEN) {
            validateUser(post.getAuthor().getId(), authorId);
        }

        // 댓글 검증
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        
        // 댓글 작성자 검증
        validateUser(comment.getAuthor().getId(), authorId);

        commentRepository.delete(comment);
    }
}
