package com.example.muscle_market.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EllenApiClient {

    @Value("${ellen.client-id}")
    private String clientId;

    @Value("${ellen.api-url}")
    private String apiUrl;

    @Value("${ellen.reset-url}")
    private String resetUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 앨런에게 질문
    public String askEllen(String content) {
        WebClient client = WebClient.builder().baseUrl(apiUrl).build();

        String response = client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("client_id", clientId)
                        .queryParam("content", content)
                        .build())
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("answer").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "앨런 API 호출 실패";
        }
    }

    // 상태 초기화
    public void resetState() {
        WebClient client = WebClient.builder().baseUrl(resetUrl).build();

        client.method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder.build())
                .header("Content-Type", "application/json")
                .bodyValue("{\"client_id\":\"" + clientId + "\"}")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
