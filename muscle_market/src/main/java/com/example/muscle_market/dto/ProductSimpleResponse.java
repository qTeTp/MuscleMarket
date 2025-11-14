package com.example.muscle_market.dto;

import com.example.muscle_market.domain.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSimpleResponse {
    private Long id;
    private String title;
    private Long price;
    private String location;


    public ProductSimpleResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.location = product.getLocation();
    }
}
