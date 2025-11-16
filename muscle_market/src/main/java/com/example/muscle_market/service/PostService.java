package com.example.muscle_market.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.muscle_market.domain.Post;
import com.example.muscle_market.domain.PostImage;
import com.example.muscle_market.domain.Sport;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.dto.BungaeDetails;
import com.example.muscle_market.dto.CreatePostDto;
import com.example.muscle_market.dto.PostDetailDto;
import com.example.muscle_market.dto.PostSummaryDto;
import com.example.muscle_market.dto.UpdatePostDto;
import com.example.muscle_market.enums.PostStatus;
import com.example.muscle_market.exception.InvalidRequestException;
import com.example.muscle_market.repository.PostRepository;
import com.example.muscle_market.repository.PostSpecification;
import com.example.muscle_market.repository.SportRepository;
import com.example.muscle_market.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SportRepository sportRepository;

    // 작성자-로그인유저 검증
    private void validateUser(Long authorId, Long userId) {
        if(!authorId.equals(userId)) {
            throw new AccessDeniedException("사용자와 작성자가 일치하지 않습니다.");
        }
    }

    // 게시글 조회 권한 확인
    private void validatePostAccess(Post post, Long curUserId) {
        if (post.getPostStatus() == PostStatus.DELETED) throw new EntityNotFoundException("삭제된 게시글입니다.");

        if (post.getPostStatus() == PostStatus.HIDDEN) {
            if (curUserId == null || !post.getAuthor().getId().equals(curUserId)) {
                throw new AccessDeniedException("이 게시글을 볼 권한이 없습니다.");
            }
        }
    }
    
    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> getAllPosts(Pageable pageable, Long curUserId, Long sportId, Boolean isBungae, String keyword) {
        Specification<Post> spec = PostSpecification.filterAndSearch(curUserId, sportId, isBungae, keyword);
        Page<Post> postPage = postRepository.findAll(spec, pageable);
        // Post 엔티티를 PostSummaryDto로 매핑
        Page<PostSummaryDto> dtoPage = postPage.map(p -> {
            return PostSummaryDto.fromEntity(p);
        });
        return dtoPage;
    }

    // 게시글 상세 조회
    public PostDetailDto getPostDetail(Long postId, Long curUserId) {
        Post post = postRepository.findByIdWithImages(postId)
            .orElseThrow(() -> new EntityNotFoundException("post not found"));
        
        // 작성자 찾을 수 없으면 에러 발생
        // TODO: 탈퇴 유저의 경우 핸들링이 필요함
        if (post.getAuthor() == null) throw new EntityNotFoundException("author not found");
        // 삭제된 글은 볼 수 없고, 숨김이면 본인만 볼 수 있어야 함
        validatePostAccess(post, curUserId);
        Post prevPost = postRepository.findFirstByPostIdLessThanOrderByPostIdDesc(postId).orElseGet(null);
        Post nextPost = postRepository.findFirstByPostIdGreaterThanOrderByPostIdAsc(postId).orElseGet(null);
        return PostDetailDto.fromEntity(post, prevPost, nextPost);
    }

    // 게시글 작성
    @Transactional
    public PostDetailDto createPost(Long authorId, CreatePostDto request) {
        // TODO: CustomUserDetails를 통째로 받을지 레포지토리에서 유저를 찾을지 나중에 확인 필요
        // 유저 검증
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 운동 카테고리 검증
        Sport sport = sportRepository.findByName(request.getSportName())
            .orElseThrow(() -> new EntityNotFoundException("Sport not found"));


        // 게시글 생성
        Post post = null;
        // 번개글 작성인지 확인
        if (request.getIsBungae() != null && request.getIsBungae()) {
            // 번개글인데 필요한 필드들이 하나라도 비어 있으면 에러 발생
            if ((request.getBungaeLocation() != null && !request.getBungaeLocation().isBlank()) 
                && request.getMaxParticipants() != null && request.getCurParticipants() != null 
                && (request.getBungaeDatetime() != null && !request.getBungaeDatetime().isBlank())) {
                    post = Post.createBungaePost(request.getTitle(), request.getContent(), author, sport, request.toBungaeDetails());
            } else throw new InvalidRequestException("번개 모임에 필요한 정보가 누락되었습니다.");
        } else {
            post = Post.createNormalPost(request.getTitle(), request.getContent(), author, sport);
        }
        // 제대로 생성 됐는지 확인
        if (post == null) throw new IllegalArgumentException("Something went wrong");

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
        Post prevPost = postRepository.findFirstByPostIdLessThanOrderByPostIdDesc(post.getPostId()).orElseGet(null);
        Post nextPost = postRepository.findFirstByPostIdGreaterThanOrderByPostIdAsc(post.getPostId()).orElseGet(null);
        return PostDetailDto.fromEntity(savedPost, prevPost, nextPost);
    }

    // 게시글 수정
    @Transactional
    public PostDetailDto updatePost(Long curUserId, Long postId, UpdatePostDto request) {
        // 게시글 db 조회
        Post post = postRepository.findByIdWithImages(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // TODO: CustomUserDetails를 통째로 받을지 레포지토리에서 유저를 찾을지 나중에 확인 필요
        // 유저 검증 및 작성자 인증 검사
        User curUser = userRepository.findById(curUserId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        validateUser(post.getAuthor().getId(), curUserId);
        
        // 게시글 접근 권한 확인
        validatePostAccess(post, curUserId);

        // 운동 카테고리 검증
        Sport sport = sportRepository.findByName(request.getSportName())
            .orElseThrow(() -> new EntityNotFoundException("Sport not found"));

        // 게시글 제목, 내용, 운동 수정
        // 번개글인지 확인하고 맞다면 변경사항 저장
        if (post.getIsBungae() != null && post.getIsBungae()) {
            if ((request.getBungaeLocation() != null && !request.getBungaeLocation().isBlank()) 
                && request.getMaxParticipants() != null && request.getCurParticipants() != null 
                && (request.getBungaeDatetime() != null && !request.getBungaeDatetime().isBlank())) {
                    post.updateBungae(request.getTitle(), request.getContent(), sport, request.toBungaeDetails());
            } else throw new InvalidRequestException("번개 모임에 필요한 정보가 누락되었습니다.");
        } else {
            post.updatePost(request.getTitle(), request.getContent(), sport);
        }
        
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

        Post prevPost = postRepository.findFirstByPostIdLessThanOrderByPostIdDesc(postId).orElseGet(null);
        Post nextPost = postRepository.findFirstByPostIdGreaterThanOrderByPostIdAsc(postId).orElseGet(null);
        return PostDetailDto.fromEntity(post, prevPost, nextPost);
    }   

    // 게시글 조회수 증가
    @Transactional
    public void increaseView(Long postId) {
        postRepository.increaseView(postId);
    }

    // 게시글 숨김
    @Transactional
    public void hidePost(Long postId, Long curUserId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        
        // 유저 검증 및 작성자 인증 검사
        validateUser(post.getAuthor().getId(), curUserId);
        
        // 게시글 접근 권한 확인
        validatePostAccess(post, curUserId);

        post.hidePost();
    }

    // 게시글 숨김 해제
    @Transactional
    public void unhidePost(Long postId, Long curUserId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        
        // 유저 검증 및 작성자 인증 검사
        validateUser(post.getAuthor().getId(), curUserId);
        
        // 게시글 접근 권한 확인
        validatePostAccess(post, curUserId);

        post.unhidePost();
    }


    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long curUserId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        
        // 유저 검증 및 작성자 인증 검사
        validateUser(post.getAuthor().getId(), curUserId);
        
        // 게시글 접근 권한 확인
        validatePostAccess(post, curUserId);
        
        postRepository.delete(post);
    }

    // 게시글이 번개인지 판별
    public boolean isBungae(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        
        return Boolean.TRUE.equals(post.getIsBungae());
    }
}
