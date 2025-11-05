//package com.example.muscle_market.controller.api;
//
//import java.security.Principal;
//
//import com.example.muscle_market.domain.CustomUserDetails;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//
//import com.example.muscle_market.domain.User;
//import com.example.muscle_market.dto.ChatMessageRequest;
//import com.example.muscle_market.service.ChatService;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequiredArgsConstructor
//public class ChatSocketController {
//    private final ChatService chatService;
//
//    // /pub/chats/messages를 통해 들어오는 메시지를 받아서 송출
//    @MessageMapping("/chats/messages")
//    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
//        if (principal == null) throw new AccessDeniedException("인증되지 않은 사용자 입니다.");
//
//        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
//
//        User user = userDetails.getUser();
//        chatService.sendMessage(request, user.getId());
//    }
//}
