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

    @Builder
    public CreateChatDto(List<Long> participantIds, String initialMessage, String chatTitle) {
        this.participantIds = participantIds;
        this.initialMessage = initialMessage;
        this.chatTitle = chatTitle;
    }
}
