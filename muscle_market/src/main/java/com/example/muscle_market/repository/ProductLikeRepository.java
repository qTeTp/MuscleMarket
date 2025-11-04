package com.example.muscle_market.repository;

import com.example.muscle_market.domain.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    // 좋아요 수 계산
    Long countByProductId(Long productId);

    // 찜하기 어떤 사용자가 특정 상품을 골랐는지
    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);

    // 사용자가 찜한 목록 조회
    List<ProductLike> findAllByUserId(Long userId);

    // 사용자가 찜한 제품 목록 페이징해서 조회
    @Query("SELECT l FROM ProductLike l JOIN FETCH l.product p JOIN FETCH p.sport s WHERE l.user.id = :userId ORDER BY l.id DESC")
    Page<ProductLike> findAllByUserWithProductAndSport(@Param("userId") Long userId, Pageable pageable);
}
