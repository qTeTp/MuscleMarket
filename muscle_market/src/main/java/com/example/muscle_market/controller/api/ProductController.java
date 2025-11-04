package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.ProductDetailDto;
import com.example.muscle_market.dto.ProductListDto;
import com.example.muscle_market.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    // 제품 리스트 조회
    @GetMapping
    public ResponseEntity<List<ProductListDto>> getProductList() {
        List<ProductListDto> products = productService.getProductList();
        // 200 신호, 리스트 반환
        return ResponseEntity.ok(products);
    }

    // 제품 상세 조회
    @GetMapping("/{productId}")
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
}
