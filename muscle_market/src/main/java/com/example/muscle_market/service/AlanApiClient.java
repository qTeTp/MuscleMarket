package com.example.muscle_market.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AlanApiClient {

    @Value("${alan.client-id}")
    private String clientId;

    // baseUrl은 도메인까지만 (경로 중복 방지)
    @Value("${alan.api-url}")
    private String apiUrl; // ex: https://kdt-api-function.azurewebsites.net

    @Value("${alan.reset-url}")
    private String resetUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 앨런에게 질문 (GET 요청)
    public String askAlan(String content) {

        String systemPrompt = """
                당신은 운동용품 추천 챗봇입니다.
                사용자의 질문을 분석해 필요한 운동 종목과 해당 종목에 필요한 용품을 추천합니다.
                
                규칙:
                1. 추천해야 할 제품명은 반드시 **별표 두 개로 감싸 강조해서** 작성합니다.
                2. 제품명은 가능한 한 정확한 명칭으로 작성합니다.
                3. 여러 제품이 있다면 리스트 형태로 나열합니다.
                4. 설명은 간단하게 하고, 강조된 제품명 중심으로 안내합니다.
                
                사용자의 질문:
                """ + content;

        WebClient client = WebClient.builder()
                .baseUrl(apiUrl) // baseUrl에 path 포함 X
                .build();

        try {
            // GET 요청
            String response = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/question") // 여기서 path 지정
                            .queryParam("client_id", clientId)
                            .queryParam("content", systemPrompt)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Raw Alan API response: " + response);

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
                .baseUrl(resetUrl) // 이미 https://.../api/v1/reset-state 포함
                .build();

        try {
            client.method(HttpMethod.DELETE) // DELETE 요청
                    .uri("") // baseUrl에 이미 경로 포함 시 빈 문자열
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("client_id", clientId)) // body 전달
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            System.out.println("Alan 상태 초기화 성공");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Alan 상태 초기화 실패: " + e.getMessage());
        }
    }

}
