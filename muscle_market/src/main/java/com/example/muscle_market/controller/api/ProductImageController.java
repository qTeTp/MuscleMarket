package com.example.muscle_market.controller.api;

import com.example.muscle_market.domain.ProductImage;
import com.example.muscle_market.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/upload")
    public ResponseEntity<ProductImage> uploadPhoto(
            @RequestParam("productId") Long productId,
            @RequestParam("file") MultipartFile file) {
        try {
            ProductImage photo = productImageService.uploadPhoto(productId, file);
            return ResponseEntity.ok(photo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductImage>> getAllPhotos() {
        List<ProductImage> photos = productImageService.getAllPhotos();
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductImage> getPhoto(@PathVariable Long id) {
        return productImageService.getPhoto(id)
                .map(photo -> ResponseEntity.ok(photo))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePhoto(@PathVariable("id") Long id) {
        try {
            productImageService.deletePhoto(id);
            return ResponseEntity.ok("사진이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
