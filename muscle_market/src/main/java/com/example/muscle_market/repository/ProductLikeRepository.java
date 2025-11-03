package com.example.muscle_market.repository;

import com.example.muscle_market.domain.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    // 좋아요 수 계산
    Long countByProductId(Long productId);
}
