//package com.example.muscle_market.controller.api;
//
//import java.util.List;
//
//import com.example.muscle_market.domain.CustomUserDetails;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.muscle_market.domain.User;
//import com.example.muscle_market.dto.ChatMessageResponse;
//import com.example.muscle_market.dto.ChatResponseDto;
//import com.example.muscle_market.dto.ChatUserDto;
//import com.example.muscle_market.dto.CreateChatDto;
//import com.example.muscle_market.service.ChatService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class ChatController {
//    private final ChatService chatService;
//
//    // 채팅방 목록 조회
//    @GetMapping("/me/chats")
//    public ResponseEntity<List<ChatResponseDto>> getChats(@AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        List<ChatResponseDto> chatRooms = chatService.getChatByUserId(user.getId());
//        return ResponseEntity.ok(chatRooms);
//    }
//
//    // 채팅방 생성
//    @PostMapping("/chats")
//    public ResponseEntity<ChatResponseDto> createChat(@RequestBody CreateChatDto request, @AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        ChatResponseDto response = chatService.createChat(request, user.getId());
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//    // 채팅방 나가기
//    @DeleteMapping("/chats/{chatId}")
//    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        chatService.leaveChatRoom(user.getId(), chatId);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 채팅방 접속했을 때 이전 메시지 불러오기
//    @GetMapping("/chats/{chatId}/messages")
//    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        return ResponseEntity.ok(chatService.findMessagesByChatId(chatId, user.getId()));
//    }
//
//    // 채팅 읽음
//    @PostMapping("/chats/{chatId}/read")
//    public ResponseEntity<Void> readMessage(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        chatService.updateLastReadAt(chatId, user.getId());
//        return ResponseEntity.ok().build();
//    }
//
//    // 채팅방 참가자 확인
//    @GetMapping("/chats/{chatId}/participants")
//    public ResponseEntity<List<ChatUserDto>> getParticipants(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        return ResponseEntity.ok(chatService.getParticipants(chatId, user.getId()));
//    }
//}
