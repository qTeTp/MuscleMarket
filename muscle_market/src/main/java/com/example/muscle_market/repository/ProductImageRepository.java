package com.example.muscle_market.repository;

import com.example.muscle_market.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // 제품의 썸네일 이미지 조회
    // image_id가 가장 낮은 이미지 URL이 올라가게 된다
    @Query("SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.id ASC LIMIT 1")
    Optional<String> findThumbnailUrlByProductId(@Param("productId") Long productId);

    // 게시글 상세 조회 시 사용
    // 특정 product에 연결된 모든 이미지 URL 리스트 조회
    List<ProductImage> findAllByProductIdOrderByIdAsc(Long productId);

    // 이미지 url 문자열만 조회
    @Query("SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.id ASC")
    List<String> findAllImageUrlsByProductId(@Param("productId") Long productId);
}
