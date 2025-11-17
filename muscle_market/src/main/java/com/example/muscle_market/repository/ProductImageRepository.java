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
    Optional<ProductImage> findFirstByProductIdOrderByIdAsc(Long productId);

    // 게시글 상세 조회 시 사용
    // 특정 product에 연결된 모든 이미지 URL 문자열 리스트 조회
    @Query("SELECT pi.s3Url FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.id ASC")
    List<String> findAllImageUrlsByProductId(@Param("productId") Long productId);

    @Query("SELECT pi.id FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.id ASC")
    List<Long> findAllImageIdsByProductId(@Param("productId") Long productId);

    // product에 연결된 모든 이미지 엔티티 리스트 조회
    List<ProductImage> findAllByProductIdOrderByIdAsc(Long productId);
}
