package com.example.muscle_market.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProductImagePageController {
    @GetMapping("/products/images")
    public String ProductImagePage() {
        return "product_image";
    }
}
