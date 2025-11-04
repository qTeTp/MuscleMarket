package com.example.muscle_market.controller.view;

import com.example.muscle_market.dto.ProductListDto;
import com.example.muscle_market.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductPageController {
    private final ProductService productService;

    // 한 페이지에 표시할 아이템 수 (가로 4줄 * 세로 5줄 = 20개)
    private static final int PAGE_SIZE = 20;

    @GetMapping("/products/list")
    public String productList(
            // 현재 페이지 번호 (0부터 시작, 기본값 0)
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long sportId,
            Model model) {

        // Pageable 객체 생성
        // 현재 페이지, 페이지 크기, 정렬 기준 (최신순)
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<ProductListDto> productPage = productService.getProductList(Optional.ofNullable(sportId), pageable);

        // 데이터 전달
        model.addAttribute("productPage", productPage);

        // 페이지네이션 처리를 위한 정보 추가
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "productlist";
    }
}
