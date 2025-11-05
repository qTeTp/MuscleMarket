package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.ProductCreateDto;
import com.example.muscle_market.dto.ProductDetailDto;
import com.example.muscle_market.dto.ProductListDto;
import com.example.muscle_market.dto.ProductUpdateDto;
import com.example.muscle_market.service.ProductLikeService;
import com.example.muscle_market.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductApiController {
    private final ProductService productService;
    private final ProductLikeService productLikeService;

    // 한 페이지당 요청 데이터 수
    // 가로 4 * 세로 5
    private static final int DEFAULT_PAGE_SIZE = 20;

    // 제품 리스트 조회
    @GetMapping("/products")
    public ResponseEntity<Page<ProductListDto>> getProducts(
            // sportId를 Optional로 처리
            // 있을 수도, 없을 수도
            @RequestParam(required = false) Long sportId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 서비스 호출
        // sportId가 있으면 필터링, 없으면 전체 조회
        Page<ProductListDto> productPage = productService.getProductList(Optional.ofNullable(sportId), pageable);

        return ResponseEntity.ok(productPage);
    }

    // 제품 상세 조회
    @GetMapping("/products/detail/{productId}")
    public ResponseEntity<ProductDetailDto> getProductDetail(@PathVariable Long productId) {
        try {
            ProductDetailDto dto = productService.getProductDetail(productId);
            // 200 신호, 상세 정보 반환
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            // 제품이 없으면 404
            return ResponseEntity.notFound().build();
        }
    }

    // 종목 카테고리 게시물 페이지 반환
    @GetMapping("/products/{sport_idx}")
    public ResponseEntity<Page<ProductListDto>> getProductsBySport(
            @PathVariable("sport_idx") Long sportId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {

        // Pageable 객체, 최신순 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 단일화된 서비스 메서드 호출
        Page<ProductListDto> productPage = productService.getProductList(Optional.ofNullable(sportId), pageable);

        // 404 및 정상 결과 반환
        if (productPage.isEmpty() && page > 0) {
            // 빈 화면 반환
            return ResponseEntity.ok(productPage);
        }
        // 정상 결과
        return ResponseEntity.ok(productPage);
    }

    // 찜 리스트 페이지 반환
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<Page<ProductListDto>> getLikedProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {

        // Pageable 객체 생성
        // 찜 목록은 id 오름차순 장렬 - 레포에서 처리 완
        Pageable pageable = PageRequest.of(page, size);

        // 서비스 호출
        Page<ProductListDto> likedProductsPage = productLikeService.getLikedProducts(userId, pageable);

        return ResponseEntity.ok(likedProductsPage);
    }

    // 게시물 등록
    @PostMapping(value = "/products", consumes = {"multipart/form-data"})
    public ResponseEntity<Long> createProduct(
            @RequestPart("request") ProductCreateDto request, // JSON 데이터
            @RequestPart("images") List<MultipartFile> imageFiles) { // 이미지 파일 리스트

        if (imageFiles == null || imageFiles.isEmpty()) {
            // 이미지 없을 시 디폴트 이미지 추가
            // 추후 구현
        }

        Long productId = productService.createProduct(request, imageFiles);

        // 201 Created 응답, 생성된 게시글의 ID 반환
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    // 게시물 수정
    @PutMapping(value = "/products/{productId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Long> updateProduct(
            @PathVariable Long productId,
            @RequestPart("request") ProductUpdateDto request,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImageFiles) {

        // newImageFiles가 null인 경우
        List<MultipartFile> images = newImageFiles != null ? newImageFiles : List.of();

        Long updatedId = productService.updateProduct(productId, request, images);

        return ResponseEntity.ok(updatedId);
    }
}

