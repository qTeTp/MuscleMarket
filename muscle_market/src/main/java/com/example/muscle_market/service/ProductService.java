package com.example.muscle_market.service;

import com.example.muscle_market.domain.Product;
import com.example.muscle_market.dto.ProductDetailDto;
import com.example.muscle_market.dto.ProductListDto;
import com.example.muscle_market.repository.ProductImageRepository;
import com.example.muscle_market.repository.ProductLikeRepository;
import com.example.muscle_market.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductLikeRepository productLikeRepository;

    @Transactional(readOnly = true)
    public List<ProductListDto> getProductList() {
        // 모든 Product 엔티티를 조회
        List<Product> products = productRepository.findAll();

        // DTO 변환
        return products.stream().map(product -> {
            // 좋아요 수 계산
            long likeCount = productLikeRepository.countByProductId(product.getId());

            // 썸네일 이미지 URL 조회 (id가 가장 낮은 이미지)
            String thumbnailUrl = productImageRepository
                    .findThumbnailUrlByProductId(product.getId())
                    .orElse(null); // 이미지가 없을 경우

            return ProductListDto.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .location(product.getLocation())
                    .views(product.getViews())
                    .likeCount(likeCount)
                    .thumbnailUrl(thumbnailUrl)
                    .sportName(product.getSport().getName())
                    .createdAt(product.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    // 제품 상세 정보 조회
    @Transactional // 조회수 증가 때문에 트랜잭션을 ReadOnly = false로 설정
    public ProductDetailDto getProductDetail(Long productId) {
        // Product 엔티티 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다. ID: " + productId));

        // 조회수 1 증가
        product.setViews();

        // 좋아요 수 계산
        long likeCount = productLikeRepository.countByProductId(productId);

        // 모든 이미지 URL 조회
        List<String> imageUrls = productImageRepository.findAllImageUrlsByProductId(productId);

        // DTO로 변환하여 반환
        return ProductDetailDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .location(product.getLocation())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .views(product.getViews())
                .likeCount(likeCount)
                .authorName(product.getUser().getName())
                .sportName(product.getSport().getName())
                .imageUrls(imageUrls)
                .build();
    }

    // 페이지에서 제품 리스트 조회
    public Page<ProductListDto> getProductList(Pageable pageable) {
        Page<Product> productPage = productRepository.findAllWithSport(pageable);

        // DTO로 변환
        return productPage.map(product -> {
            long likeCount = productLikeRepository.countByProductId(product.getId());
            String thumbnailUrl = productImageRepository
                    .findThumbnailUrlByProductId(product.getId())
                    .orElse("default_image.jpg"); // 기본 이미지 설정

            return ProductListDto.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .location(product.getLocation())
                    .views(product.getViews())
                    .likeCount(likeCount)
                    .thumbnailUrl(thumbnailUrl)
                    .sportName(product.getSport().getName())
                    .createdAt(product.getCreatedAt())
                    .build();
        });
    }
}
