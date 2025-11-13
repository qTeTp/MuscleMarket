package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.AlanChatResponse;
import com.example.muscle_market.service.ProductRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alan")
public class AlanChatController {

    private final ProductRecommendationService recommendationService;

    public AlanChatController(ProductRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // 챗봇 질문
    @GetMapping("/chat")
    public ResponseEntity<AlanChatResponse> chat(@RequestParam String content){
        System.out.println("사용자 질문 : " + content);

        AlanChatResponse response = recommendationService.askAndRecommend(content);

        // 콘솔에 결과 출력
        System.out.println("앨런 답변 : " + response.getAlanAnswer());

        return ResponseEntity.ok(response);
    }

    // 상태 초기화
    @DeleteMapping("/reset")
    public ResponseEntity<String> reset() {
        recommendationService.resetAlanState();
        System.out.println("앨런 상태 초기화 완료");
        return ResponseEntity.ok("앨런 상태 초기화 완료");
    }
}
