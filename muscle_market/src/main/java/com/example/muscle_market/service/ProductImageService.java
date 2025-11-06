package com.example.muscle_market.service;

import com.example.muscle_market.domain.ProductImage;
import com.example.muscle_market.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final S3Service s3Service;
    private final ProductImageRepository productImageRepository;

    public ProductImage uploadPhoto(Long productId, MultipartFile file) throws IOException {
        // S3에 파일 업로드
        String s3Url = s3Service.uploadFile(file);
        String s3Key = extractKeyFromUrl(s3Url);

        // 데이터베이스에 메타데이터 저장
        ProductImage productImage = ProductImage.builder()
                .productId(productId)
                .createdAt(LocalDateTime.now())
                .s3Key(s3Key)
                .s3Url(s3Url)
                .originalFilename(file.getOriginalFilename())
                .build();
        return productImageRepository.save(productImage);
    }

    private String extractKeyFromUrl(String url) {
        // URL에서 S3 키 추출
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public List<ProductImage> getAllPhotos() {
        return productImageRepository.findAll();
    }

    public Optional<ProductImage> getPhoto(Long id) {
        return productImageRepository.findById(id);
    }

    public void deletePhoto(Long id) throws Exception {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new Exception("사진을 찾을 수 없습니다."));

        // S3에서 파일 삭제
        s3Service.deleteFile(productImage.getS3Key());
        // 데이터베이스에서 레코드 삭제
        productImageRepository.deleteById(id);
    }

}
