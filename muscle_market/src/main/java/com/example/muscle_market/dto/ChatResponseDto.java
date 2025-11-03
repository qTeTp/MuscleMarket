package com.example.muscle_market.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatResponseDto {
    private Long chatId;
    private List<ChatUserDto> chatUsers;
    private String chatTitle;
    private String lastMessage;
    private LocalDateTime lastMessageSentAt;
    private Long productId;
    @Setter
    private Long unreadCount;

    @Builder
    public ChatResponseDto(Long chatId, List<ChatUserDto> chatUsers, String chatTitle, String lastMessage, LocalDateTime lastMessageSentAt, Long productId, Long unreadCount) {
        this.chatId = chatId;
        this.chatUsers = chatUsers;
        this.chatTitle = chatTitle;
        this.lastMessage = lastMessage;
        this.lastMessageSentAt = lastMessageSentAt;
        this.productId = productId;
        this.unreadCount = unreadCount;
    }
}
