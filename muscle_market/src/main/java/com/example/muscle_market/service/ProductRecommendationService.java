package com.example.muscle_market.service;

import com.example.muscle_market.domain.Product;
import com.example.muscle_market.domain.Sport;
import com.example.muscle_market.dto.AlanChatResponse;
import com.example.muscle_market.dto.ProductSimpleResponse;
import com.example.muscle_market.repository.ProductRepository;
import com.example.muscle_market.repository.SportRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {

    private final AlanApiClient alanApiClient;
    private final ProductRepository productRepository;
    private final SportRepository sportRepository;

    public ProductRecommendationService(AlanApiClient alanApiClient,
                                        ProductRepository productRepository,
                                        SportRepository sportRepository) {
        this.alanApiClient = alanApiClient;
        this.productRepository = productRepository;
        this.sportRepository = sportRepository;
    }

    // 앨런 답변 기반 키워드로 DB 검색
    public AlanChatResponse askAndRecommend(String content) {
        // 앨런 API 호출
        String alanAnswer = alanApiClient.askAlan(content);


// 스포츠 테이블에서 비교하는 방법

//        // 모든 스포츠 불러오기
//        List<Sport> allSports = sportRepository.findAll();
//
//        // 앨런 답변에 포함된 운동종목 찾기
//        Sport matchedSport = null;
//        for (Sport sport : allSports) {
//            if (alanAnswer.contains(sport.getName())) {
//                matchedSport = sport;
//                break;
//            }
//        }
//
//        // 운동 종목에 해당하는 물품 게시글 불러오기
//        List<Product> recommendProducts = new ArrayList<>();
//        if (matchedSport != null) {
//            recommendProducts = productRepository.findBySportId(matchedSport.getId());
//            System.out.println("추천된 운동 종목 : " + matchedSport.getName() );
//        } else {
//            System.out.println("매칭되는 운동 종목이 없습니다.");
//        }
//
//        // 콘솔에 추천 상품 출력
//        if (!recommendProducts.isEmpty()) {
//            System.out.println("추천 상품 목록 : ");
//            for (Product p : recommendProducts) {
//                System.out.println("- " + p.getTitle() + " / " + p.getPrice() + "원");
//            }
//        }

// 앨런 강조된 부분에서 키워드 추출

        // 앨런 답변에서 **...** 부분 추출하기
        List<String> keywords = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\*\\*(.*?)\\*\\*").matcher(alanAnswer);
        while (matcher.find()) {
            keywords.add(matcher.group(1));
        }

        // 중복 제거 후 DB 검색
        Set<Product> recommendProductsSet = new HashSet<>();
        for (String keyword : keywords) {
            List<Product> matched = productRepository.findByTitleContainingIgnoreCase(keyword);
            recommendProductsSet.addAll(matched);
        }

        // List로 변환
        List<ProductSimpleResponse> recommendProductsDto = recommendProductsSet.stream()
                .map(ProductSimpleResponse::new)
                .collect(Collectors.toList());

        // 콘솔 출력
        System.out.println("앨런 답변 : " + alanAnswer);
        System.out.println("추천 상품 : ");
        for (ProductSimpleResponse p :  recommendProductsDto) {
            System.out.println("- " + p.getTitle() + " / " + p.getPrice() + "원");
        }

        // 결과 반환
        return new AlanChatResponse(alanAnswer, recommendProductsDto);
    }

    // 상태 초기화
    public void resetAlanState(){
        alanApiClient.resetState();
    }
}
