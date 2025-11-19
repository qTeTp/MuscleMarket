package com.example.muscle_market.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.muscle_market.domain.Post;
import com.example.muscle_market.enums.PostStatus;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    // id로 탐색할 때 포스트 이미지들도 로드하는 경우 사용
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.postImages WHERE p.postId = :postId")
    Optional<Post> findByIdWithImages(@Param("postId") Long postId);

    // 게시글 조회수 증가
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.views = COALESCE(p.views, 0) + 1 WHERE p.id = :postId")
    void increaseView(@Param("postId") Long postId);

    // 이전 게시글 탐색
    Optional<Post> findFirstByPostIdLessThanAndPostStatusOrderByPostIdDesc(Long postId, PostStatus postStatus);

    // 다음 게시글 탐색
    Optional<Post> findFirstByPostIdGreaterThanAndPostStatusOrderByPostIdAsc(Long postId, PostStatus postStatus);
}
