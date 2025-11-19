package com.example.muscle_market.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponse {
    private Long chatId;
    private SimplifiedUserDto sender;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    @Builder
    public ChatMessageResponse(Long chatId, SimplifiedUserDto sender, String content, LocalDateTime sentAt) {
        this.chatId = chatId;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }
}
