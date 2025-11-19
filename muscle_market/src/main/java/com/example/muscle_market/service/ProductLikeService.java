package com.example.muscle_market.service;

import com.example.muscle_market.domain.Product;
import com.example.muscle_market.domain.ProductImage;
import com.example.muscle_market.domain.ProductLike;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.dto.ProductListDto;
import com.example.muscle_market.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductImageRepository productImageRepository; // 추가
    private final ProductLikeRepository productLikeRepository;

    // 찜하기 추가 | 취소
    @Transactional
    public boolean toggleLike(Long userId, Long productId) {

        // 이미 찜했는지 확인
        Optional<ProductLike> existingLike = productLikeRepository.findByUserIdAndProductId(userId, productId);

        if (existingLike.isPresent()) {
            // 이미 찜했다면 -> 취소
            productLikeRepository.delete(existingLike.get());
            return false;
        } else {
            // 없다면 찜
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            ProductLike newLike = ProductLike.builder()
                    .user(user)
                    .product(product)
                    .build();

            productLikeRepository.save(newLike);
            return true;
        }
    }

    // 페이지에서 찜 목록 조회
    // productListDto랑 같이 씀
    @Transactional(readOnly = true)
    public Page<ProductListDto> getLikedProducts(Long userId, Pageable pageable) {
        // Repository 찜 목록 조회
        Page<ProductLike> likePage = productLikeRepository.findAllByUserWithProductAndSport(userId, pageable);

        // 페이지에서 사용할 dto로 변환
        return likePage.map(like -> {
            Product product = like.getProduct();

            // DTO 생성에 필요한 추가 정보 조회
            long likeCount = productLikeRepository.countByProductId(product.getId());
            String thumbnailUrl = productImageRepository.findFirstByProductIdOrderByIdAsc(product.getId())
                    .map(ProductImage::getS3Url) // ProductImage 엔티티의 s3Url 필드 사용
                    .orElse("default_image.jpg");

            // ProductListDto로 매핑
            return ProductListDto.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .location(product.getLocation())
                    .sportName(product.getSport().getName())
                    .views(product.getViews())
                    .status(product.getStatus())
                    .likeCount(likeCount)
                    .thumbnailUrl(thumbnailUrl)
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .build();
        });
    }
}
