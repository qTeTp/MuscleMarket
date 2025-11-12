package com.example.muscle_market.controller.view;

import com.example.muscle_market.domain.CustomUserDetails;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.dto.*;
import com.example.muscle_market.service.ProductService;
import com.example.muscle_market.service.SportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductPageController {
    private final ProductService productService;
    private final SportService sportService;

    // 한 페이지에 표시할 아이템 수 (가로 4줄 * 세로 5줄 = 20개)
    private static final int PAGE_SIZE = 20;

    @GetMapping
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

    // 신규 게시물 등록
    @GetMapping("/new")
    public String newProductForm(Model model, @AuthenticationPrincipal CustomUserDetails authUser) {
        if (authUser != null) {
            SimplifiedUserDto currentUser = SimplifiedUserDto.builder()
                    .userId(authUser.getId())
                    .username(authUser.getUsername())
                    .nickname(authUser.getNickname())
                    .profileImgUrl(authUser.getProfileImgUrl())
                    .build();
            model.addAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("currentUser", null);
        }

        // sport 목록
        model.addAttribute("sports", sportService.getAllSports());
        return "product_form";
    }

    // 게시물 상세 페이지 반환 매핑
    @GetMapping("/{productId}")
    public String productDetail(@PathVariable Long productId, Model model) {

        // 게시물 상세 DTO get
        ProductDetailDto dto = productService.getProductDetail(productId);
        model.addAttribute("dto", dto);

        return "product_detail";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam String keyword, // 필수 검색 키워드
            @RequestParam(required = false) Long sportId, // 선택적 카테고리 필터
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        // 서비스 호출
        Page<ProductListDto> productPage =
                productService.searchProducts(Optional.ofNullable(sportId), keyword, pageable);

        // 모델에 데이터 먹이기
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        // 검색 결과를 뷰에 전달하기 위한 추가 정보
        model.addAttribute("currentKeyword", keyword); // 검색 결과 뷰에 키워드 유지
        model.addAttribute("currentSportId", sportId); // 카테고리 필터 유지 (페이지네이션을 위해)

        // productlist.html 재사용
        return "productlist";
    }
}
