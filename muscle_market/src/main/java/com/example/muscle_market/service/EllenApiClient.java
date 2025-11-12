package com.example.muscle_market.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EllenApiClient {

    @Value("${ellen.client-id}")
    private String clientId;

    // baseUrl은 도메인까지만 (경로 중복 방지)
    @Value("${ellen.api-url}")
    private String apiUrl; // ex: https://kdt-api-function.azurewebsites.net

    @Value("${ellen.reset-url}")
    private String resetUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 앨런에게 질문 (GET 요청)
    public String askEllen(String content) {
        WebClient client = WebClient.builder()
                .baseUrl(apiUrl) // baseUrl에 path 포함 X
                .build();

        try {
            // GET 요청
            String response = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/question") // 여기서 path 지정
                            .queryParam("client_id", clientId)
                            .queryParam("content", content)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Raw Ellen API response: " + response);

            // JSON 파싱 후 "content" 필드 반환
            JsonNode root = objectMapper.readTree(response);
            return root.path("content").asText(); // "answer" → "content"

        } catch (Exception e) {
            e.printStackTrace();
            return "앨런 API 호출 실패";
        }
    }

    // 상태 초기화 (DELETE 요청)
    public void resetState() {
        WebClient client = WebClient.builder()
                .baseUrl(resetUrl)
                .build();

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("client_id", clientId)
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
