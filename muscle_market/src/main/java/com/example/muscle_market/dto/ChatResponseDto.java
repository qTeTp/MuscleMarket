package com.example.muscle_market.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.muscle_market.domain.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatResponseDto {
    private Long chatId;
    private List<SimplifiedUserDto> chatUsers;
    private String chatTitle;
    private String lastMessage;
    private LocalDateTime lastMessageSentAt;
    private ProductSummaryForChat productContent;
    @Setter
    private Long unreadCount;

    @Builder
    public ChatResponseDto(Long chatId, List<SimplifiedUserDto> chatUsers, String chatTitle, String lastMessage, LocalDateTime lastMessageSentAt, Product product, String productThumbnail, Long unreadCount) {
        this.chatId = chatId;
        this.chatUsers = chatUsers;
        this.chatTitle = chatTitle;
        this.lastMessage = lastMessage;
        this.lastMessageSentAt = lastMessageSentAt;
        this.productContent = ProductSummaryForChat.builder()
            .productId(product.getId())
            .title(product.getTitle())
//            .price(product.getPrice())
            .status(product.getStatus())
            .thumbnail(productThumbnail)
            .build();
        this.unreadCount = unreadCount;
    }
}
