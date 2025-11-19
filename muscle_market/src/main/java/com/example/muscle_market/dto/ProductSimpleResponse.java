package com.example.muscle_market.dto;

import com.example.muscle_market.domain.Product;
import com.example.muscle_market.domain.ProductImage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductSimpleResponse {
    private Long id;
    private String title;
    private Long price;
    private String location;
    // s3 이미지 url
    private List<String> imageUrls;


    public ProductSimpleResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.location = product.getLocation();

        this.imageUrls = product.getImages().stream()
                .map(ProductImage::getS3Url)
                .toList();
    }
}
