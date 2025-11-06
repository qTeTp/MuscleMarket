package com.example.muscle_market.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.muscle_market.domain.Bungae;
import com.example.muscle_market.domain.Post;
import com.example.muscle_market.domain.PostImage;
import com.example.muscle_market.domain.Sport;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.dto.CreatePostDto;
import com.example.muscle_market.dto.PostDetailDto;
import com.example.muscle_market.dto.PostSummaryDto;
import com.example.muscle_market.dto.PostUserDto;
import com.example.muscle_market.dto.UpdatePostDto;
import com.example.muscle_market.repository.BungaeRepository;
import com.example.muscle_market.repository.PostRepository;
import com.example.muscle_market.repository.PostSpecification;
import com.example.muscle_market.repository.SportRepository;
import com.example.muscle_market.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BungaeRepository bungaeRepository;
    private final SportRepository sportRepository;

    // 작성자-로그인유저 검증
    private void validateUser(Long authorId, Long userId) {
        if(!authorId.equals(userId)) {
            throw new AccessDeniedException("You are not the author");
        }
    }
    
    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> getAllPosts(Pageable pageable, Long sportId, Boolean isBungae, String keyword) {
        Specification<Post> spec = PostSpecification.filterAndSearch(sportId, isBungae, keyword);
        Page<Post> postPage = postRepository.findAll(spec, pageable);
        // Post 엔티티를 PostSummaryDto로 매핑
        Page<PostSummaryDto> dtoPage = postPage.map(p -> {
            return PostSummaryDto.fromEntity(p);
        });
        return dtoPage;
    }

    // 게시글 상세 조회
    public PostDetailDto getPostDetail(Long postId) {
        Post post = postRepository.findByIdWithImages(postId)
            .orElseThrow(() -> new IllegalArgumentException("post not found"));
        
        // 작성자 찾을 수 없으면 에러 발생
        // TODO: 탈퇴 유저의 경우 핸들링이 필요함
        if (post.getAuthor() == null) throw new IllegalArgumentException("author not found");

        return PostDetailDto.fromEntity(post);
    }

    // 게시글 작성
    @Transactional
    public PostDetailDto createPost(Long authorId, CreatePostDto request) {
        // TODO: CustomUserDetails를 통째로 받을지 레포지토리에서 유저를 찾을지 나중에 확인 필요
        // 유저 검증
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 운동 카테고리 검증
        Sport sport = sportRepository.findByName(request.getSportName())
            .orElseThrow(() -> new IllegalArgumentException("Sport not found"));

        // 번개 검증 (번개 모임 게시글이면 번개가 먼저 생성됨)
        Bungae bungae = bungaeRepository.findById(request.getBungaeId())
            .orElseGet(null);
        
        // 게시글 생성
        Post post = Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .author(author)
            .sport(sport)
            .bungae(bungae)
            .build();

        // 게시글에 이미지 추가
        List<String> imageUrls = request.getPostImages();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                PostImage postImage = PostImage.builder().imageUrl(imageUrl).build();
                post.addImage(postImage);
            }
        }
        
        // 저장 후 dto 리턴
        Post savedPost = postRepository.save(post);
        return PostDetailDto.fromEntity(savedPost);
    }

    // 게시글 수정
    @Transactional
    public PostDetailDto updatePost(Long curUserId, Long postId, UpdatePostDto request) {
        // 게시글 db 조회
        Post post = postRepository.findByIdWithImages(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // TODO: CustomUserDetails를 통째로 받을지 레포지토리에서 유저를 찾을지 나중에 확인 필요
        // 유저 검증 및 작성자 인증 검사
        User curUser = userRepository.findById(curUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        validateUser(post.getAuthor().getId(), curUserId);

        // 운동 카테고리 검증
        Sport sport = sportRepository.findByName(request.getSportName())
            .orElseThrow(() -> new IllegalArgumentException("Sport not found"));

        // 게시글 제목, 내용, 운동 수정
        post.updatePost(request.getTitle(), request.getContent(), sport);

        // 게시글 사진 수정
        List<PostImage> replacedImages = new ArrayList<>();
        Map<String, PostImage> originalImageUrls = post.getPostImages().stream()
            .collect(Collectors.toMap(PostImage::getImageUrl, image -> image));
        
        // 기존 이미지가 있으면 그대로 사용하고 없으면 새로 생성
        for (String imgUrl : request.getPostImages()) {
            if (originalImageUrls.containsKey(imgUrl)) {
                replacedImages.add(originalImageUrls.get(imgUrl));
            } else {
                PostImage postImage = PostImage.builder().imageUrl(imgUrl).build();
                replacedImages.add(postImage);
            }
        }
        
        // 삭제 대상으로 예약하고, 새 이미지들로 교체 (기존 이미지면 삭제 대상에서 알아서 제외된다고 함)
        post.getPostImages().clear();
        for (PostImage image : replacedImages) {
            post.addImage(image);
        }

        return PostDetailDto.fromEntity(post);
    }   

    // 게시글 조회수 증가
    @Transactional
    public void increaseView(Long postId) {
        postRepository.increaseView(postId);
    }


    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        postRepository.delete(post);
    }
}
