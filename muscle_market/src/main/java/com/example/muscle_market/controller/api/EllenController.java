package com.example.muscle_market.controller.api;

import com.example.muscle_market.service.EllenApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ellen")
public class EllenController {

    private final EllenApiClient ellenApiClient;

    public EllenController(EllenApiClient ellenApiClient) {
        this.ellenApiClient = ellenApiClient;
    }

    // 앨런에게 질문 보내기
    @GetMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestParam String content) {
        String answer = ellenApiClient.askEllen(content);
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    // 앨런 상태 초기화
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        ellenApiClient.resetState();
        return ResponseEntity.ok("앨런 상태 초기화 완료");
    }
}
