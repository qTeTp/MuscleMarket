package com.example.muscle_market.controller.api;

import com.example.muscle_market.service.AlanApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alan")
public class AlanController {

    private final AlanApiClient alanApiClient;

    public AlanController(AlanApiClient alanApiClient) {
        this.alanApiClient = alanApiClient;
    }

    // 앨런에게 질문 보내기
    @GetMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestParam String content) {
        String answer = alanApiClient.askAlan(content);
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    // 앨런 상태 초기화
    @DeleteMapping("/reset")
    public ResponseEntity<String> reset() {
        alanApiClient.resetState();
        return ResponseEntity.ok("앨런 상태 초기화 완료");
    }
}
