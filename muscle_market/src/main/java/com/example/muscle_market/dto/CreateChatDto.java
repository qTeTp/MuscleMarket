package com.example.muscle_market.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatDto {
    private List<Long> participantIds;
    private String initialMessage;
    private String chatTitle;
    private Long productId;

    @Builder
    public CreateChatDto(List<Long> participantIds, String initialMessage, String chatTitle, Long productId) {
        this.participantIds = participantIds;
        this.initialMessage = initialMessage;
        this.chatTitle = chatTitle;
        this.productId = productId;
    }
}
