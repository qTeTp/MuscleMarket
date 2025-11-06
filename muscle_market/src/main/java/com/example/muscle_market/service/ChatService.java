package com.example.muscle_market.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.muscle_market.domain.User;
import com.example.muscle_market.domain.UserChatRelationship;
import com.example.muscle_market.dto.*;
import com.example.muscle_market.enums.RelationshipStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.muscle_market.domain.Chat;
import com.example.muscle_market.domain.Message;

import com.example.muscle_market.repository.ChatRepository;
import com.example.muscle_market.repository.MessageRepository;
import com.example.muscle_market.repository.UserChatRelationshipRepository;
import com.example.muscle_market.repository.UserRepository;
import com.example.muscle_market.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
   private final ChatRepository chatRepository;
   private final UserChatRelationshipRepository relationshipRepository;
   private final UserRepository userRepository;
   private final MessageRepository messageRepository;
   private final ProductRepository productRepository;
   private final SimpMessageSendingOperations messagingTemplate;

   // 사용자가 속한 모든 채팅방 조회
   // 정렬은 서비스 레이어 말고 조회시 정렬
   public List<ChatResponseDto> getChatByUserId(Long userId) {
       // 사용자가 속한 모든 채팅방 id 리스트 저장
       List<Long> chatIds = relationshipRepository.findAllByUserIdWithChat(userId, RelationshipStatus.ACTIVE)
           .stream()
           .map(ucr -> ucr.getChat().getChatId())
           .toList();

       if (chatIds.isEmpty()) { return Collections.emptyList(); }

       // 채팅방 id 리스트로 모든 참여자 정보 조회 (참가자 리스트 리턴용)
       List<UserChatRelationship> allRelationships = relationshipRepository.findAllByChat_ChatIdIn(chatIds);
       Map<Long, List<ChatUserDto>> usersByChatId = allRelationships
           .stream()
           .collect(Collectors.groupingBy(
               (UserChatRelationship ucr) -> ucr.getChat().getChatId(),
               Collectors.mapping(ucr -> ChatUserDto.builder()
                   .userId(ucr.getUser().getId())
                   .nickname(ucr.getUser().getNickname())
                   .profileImageUrl(ucr.getUser().getProfileImgUrl())
                   .build(), Collectors.<ChatUserDto>toList())
           ));

       // 채팅방 id로 리스트로 각 채팅방의 마지막 메시지 정보를 조회해서 Map<chatId, Message> 형태로 저장
       List<Message> lastMessages = messageRepository.findLastMessagesByChatIds(chatIds);
       Map<Long, Message> lastMessageByChatId = lastMessages
           .stream()
           .collect(Collectors.toMap(m -> m.getChat().getChatId(), m -> m));

       // 최종적으로 ChatResponseDto 리스트 생성해서 리턴
       List<ChatResponseDto> response = chatIds
           .stream()
           .map(chatId -> {
               // 내 relationship을 찾아서 안읽은 메시지 수 확인
               UserChatRelationship myRelationship = allRelationships.stream()
                   .filter(r -> r.getUser().getId().equals(userId) && r.getChat().getChatId().equals(chatId))
                   .findFirst()
                   .orElseThrow(() -> new IllegalArgumentException("UserChatRelationship not found"));

               List<ChatUserDto> chatUsers = usersByChatId.getOrDefault(chatId, Collections.emptyList());
               Message lastMessage = lastMessageByChatId.get(chatId);

               return ChatResponseDto.builder()
                   .chatId(chatId)
                   .chatTitle(lastMessage != null ? lastMessage.getChat().getChatTitle() : null)
                   .chatUsers(chatUsers)
                   .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                   .lastMessageSentAt(lastMessage != null ? lastMessage.getCreatedAt() : null)
                   .productId(lastMessage != null ? lastMessage.getChat().getProduct().getId() : null)
                   .unreadCount(messageRepository.countUnreadMessagesAfter(chatId, myRelationship.getLastReadAt()))
                   .build();
           }).toList();

       return response;
   }

   // 채팅방 생성 (재입장 로직 포함)
   @Transactional
   public ChatResponseDto createChat(CreateChatDto request, Long myUserId) {
       // 자기 자신 채팅 초대 방지
       if (request.getParticipantIds().contains(myUserId)) {
           throw new IllegalArgumentException("자기 자신을 초대할 수 없습니다");
       }

       User me = findUserById(myUserId);
       ArrayList<User> chatUsers = request.getParticipantIds()
               .stream()
               .map(uid -> userRepository.findById(uid).orElseThrow(() -> new IllegalArgumentException("User not found")))
               .collect(Collectors.toCollection(ArrayList::new));
       chatUsers.add(me);


       ArrayList<ChatUserDto> chatUsersDto = chatUsers.stream()
               .map(u -> ChatUserDto.builder()
                       .userId(u.getId())
                       .nickname(u.getNickname())
                       .profileImageUrl(u.getProfileImgUrl())
                       .build())
               .collect(Collectors.toCollection(ArrayList::new));

       // 새 채팅방 생성 (1대1인지 단체 채팅방인지 확인)
       Chat newChat = request.getParticipantIds().size() == 1 ? findOrCreateOneToOneChat(chatUsers, request.getProductId()) : createChatEntity(chatUsers, request.getChatTitle(), request.getProductId());

       // 초기 메시지 세팅
       Message initialMessage = saveInitialMessage(newChat, me, request.getInitialMessage());

       // 알림 전송
       sendNotification(newChat, initialMessage, chatUsersDto);

       return ChatResponseDto.builder()
               .chatId(newChat.getChatId())
               .chatUsers(chatUsersDto)
               .chatTitle(newChat.getChatTitle())
               .lastMessage(initialMessage.getContent())
               .lastMessageSentAt(initialMessage.getCreatedAt())
               .productId(newChat.getProduct().getId())
               .unreadCount(0L)
               .build();
   }

   // 유저 탐색
   private User findUserById(Long userId) {
       return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user not found!"));
   }

   // 1대1 채팅방 찾아서 리턴하거나 새로 생성
   private Chat findOrCreateOneToOneChat(ArrayList<User> chatUsers, Long productId) {
       // 1대1 채팅방에서만 쓰는 메서드이기 때문에 길이가 2임을 보증
       if (chatUsers.size() != 2) throw new IllegalArgumentException("this method is only for creating 1:1 chat");
       User user1 = chatUsers.get(0);
       User user2 = chatUsers.get(1);

       // 한 명이라도 ACTIVE한지 확인
       return relationshipRepository.findActiveOneToOneChatByUsers(user1.getId(), user2.getId(), RelationshipStatus.ACTIVE)
               .map(existingChat -> {
                   UserChatRelationship relationship1 = relationshipRepository.findByUserIdAndChatId(user1.getId(), existingChat.getChatId())
                           .orElseThrow(() -> new IllegalArgumentException("relationship not found!"));
                   UserChatRelationship relationship2 = relationshipRepository.findByUserIdAndChatId(user2.getId(), existingChat.getChatId())
                           .orElseThrow(() -> new IllegalArgumentException("relationship not found!"));

                   relationship1.setStatus(RelationshipStatus.ACTIVE);
                   relationship2.setStatus(RelationshipStatus.ACTIVE);

                   return existingChat;
               })
               // 둘 다 ACTIVE한 채팅방이 없으니 새로 생성
               .orElseGet(() -> createChatEntity(chatUsers, null, productId));
   }

   private Chat createChatEntity(ArrayList<User> chatUsers, String chatTitle, Long productId) {
       Chat newChat = chatRepository.save(new Chat());
       // 그룹 채팅방이면 타이틀 지정 가능
       if (chatUsers.size() > 2) {
           if (chatTitle == null || chatTitle.isEmpty()) newChat.setChatTitle(String.format("그룹채팅방 %d", newChat.getChatId()));
           else newChat.setChatTitle(chatTitle);
       }

       // 물품 지정하기 (현재는 거래를 위한 채팅만 있으므로 거래 게시글이 없으면 에러를 발생시키도록 설계)
       // newChat.setProduct(productRepository.findById(productId).orElse(null));
       newChat.setProduct(productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("product not found")));

       chatUsers.forEach(user -> {
           relationshipRepository.save(UserChatRelationship.builder()
                   .user(user)
                   .chat(newChat)
                   .status(RelationshipStatus.ACTIVE)
                   .build());
       });
       return newChat;
   }

   // 채팅방 초기 메시지 저장
   private Message saveInitialMessage(Chat chat, User sender, String content) {
       return messageRepository.save(Message.builder()
               .chat(chat)
               .sender(sender)
               .content(content)
               .build());
   }

   // 참여자들에게 실시간 알림 전송 (채팅방 내부 + 개인 알림 채널)
   private void sendNotification(Chat chat, Message message, List<ChatUserDto> participants) {
       // 채팅방 구독
       String chatUrl = "/sub/chats/" + chat.getChatId();
       ChatUserDto senderDto = ChatUserDto.builder()
               .userId(message.getSender().getId())
               .nickname(message.getSender().getNickname())
               .profileImageUrl(message.getSender().getProfileImgUrl())
               .build();

       messagingTemplate.convertAndSend(chatUrl, ChatMessageResponse.builder()
               .chatId(chat.getChatId())
               .sender(senderDto)
               .content(message.getContent())
               .sentAt(message.getCreatedAt())
               .build()
       );

       // 유저 개인 구독
       relationshipRepository.findAllByChatId(chat.getChatId())
               .forEach(rel -> {
                   ChatResponseDto participantResponse = ChatResponseDto.builder()
                           .chatId(chat.getChatId())
                           .chatTitle(chat.getChatTitle())
                           .chatUsers(participants)
                           .lastMessage(message.getContent())
                           .lastMessageSentAt(message.getCreatedAt())
                           .productId(chat.getProduct().getId())
                           .unreadCount(messageRepository.countUnreadMessagesAfter(chat.getChatId(), rel.getLastReadAt()))
                           .build();
                   messagingTemplate.convertAndSend("/sub/users/" +  rel.getUser().getId(), participantResponse);
               });
   }

   // 채팅방 나가기
   @Transactional
   public void leaveChatRoom(Long userId, Long chatRoomId) {
       // 채팅방 존재 확인
       Chat chat = chatRepository.findById(chatRoomId)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다"));

       // 사용자가 이 채팅방의 멤버인지 확인
       UserChatRelationship myRelationship = relationshipRepository
               .findByUserIdAndChatId(userId, chatRoomId)
               .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하지 않았습니다"));

       // 그룹채팅방과 1대1 채팅방의 나가기 로직은 다름
       boolean isGroupChat = chat.getChatTitle() != null && !chat.getChatTitle().isEmpty();

       if (isGroupChat) {
           // 본인 관계 삭제
           relationshipRepository.delete(myRelationship);

           // 나머지 참여자 상태 확인
           List<UserChatRelationship> remainingRelationships = relationshipRepository.findAllByChatId(chatRoomId);

           // 내가 마지막으로 나간 사람이라면 나머지 데이터 삭제
           if (remainingRelationships.isEmpty()) {
               messageRepository.deleteAllMessagesByChatId(chatRoomId);
               chatRepository.deleteById(chatRoomId);
           } else {
               // 그룹 채팅방에 남은 유저들에게 누가 나갔다고 알림 보내기
           }

       } else {
           // 이미 나간 상태인지 확인
           if (myRelationship.getStatus() == RelationshipStatus.LEFT) {
               throw new IllegalArgumentException("이미 나간 채팅방입니다");
           }

           // 내 상태를 LEFT로 변경
           myRelationship.leaveChat();

           // 상대방 상태 확인
           List<UserChatRelationship> allRelationships = relationshipRepository.findAllByChatId(chatRoomId);

           // 모두 LEFT 상태인지 확인
           boolean allLeft = allRelationships.stream()
                   .allMatch(rel -> rel.getStatus() == RelationshipStatus.LEFT);

           if (allLeft) {
               // 모두 나갔으면 채팅방 및 관련 데이터 삭제
               // 메시지 삭제
               messageRepository.deleteAllMessagesByChatId(chatRoomId);
               // UserChatRelationship 삭제
               relationshipRepository.deleteAllByChatId(chatRoomId);
               // Chat 삭제
               chatRepository.deleteById(chatRoomId);
           }
       }
   }

   // 채팅 저장
   @Transactional
   public void sendMessage(ChatMessageRequest request, Long userId) {
       // 요청 유효성 검사 (요청을 보낸 사람이 채팅방에 속했는지 검사, 만약 문제가 있다면 에러를 throw)
       validate(request.getChatId(), userId);

       // 유저 유효성 검사
       User sender = userRepository.findById(request.getUserId())
               .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));
       // 채팅방 유효성 검사
       Chat currentChat = chatRepository.findById(request.getChatId())
               .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다"));
       // 저장
       Message savedMessage = messageRepository.save(Message.builder()
               .chat(currentChat)
               .sender(sender)
               .content(request.getContent())
               .build());

       // 1대1 채팅방 한정, 나갔던 유저를 ACTIVE 상태로 바꾸기
       List<UserChatRelationship> relationships = relationshipRepository.findAllByChatId(request.getChatId());
       if (currentChat.getChatTitle() == null) { // 1대1 채팅방은 무조건 chatTitle이 null임
           if (relationships.get(0).getUser().getId().equals(sender.getId())) {
               relationships.get(1).rejoinChat();
           } else {
               relationships.get(0).rejoinChat();
           }
       }

       List<ChatUserDto> participants = relationships.stream()
               .map(r -> ChatUserDto.builder()
                       .userId(r.getUser().getId())
                       .nickname(r.getUser().getNickname())
                       .profileImageUrl(r.getUser().getProfileImgUrl())
                       .build()).toList();

       sendNotification(currentChat, savedMessage, participants);
   }

   // 해당 채팅방 모든 메시지 조회
   public List<ChatMessageResponse> findMessagesByChatId(Long chatId, Long userId) {
       // 요청 유효성 검사 (요청을 보낸 사람이 채팅방에 속했는지 검사, 만약 문제가 있다면 에러를 throw)
       validate(chatId, userId);
       return messageRepository.findAllMessagesByChatId(chatId);
   }

   // 해당 채팅방 읽음 표시
   @Transactional
   public void updateLastReadAt(Long chatId, Long userId) {
       UserChatRelationship relationship = relationshipRepository.findByUserIdAndChatId(userId, chatId)
               .orElseThrow(() -> new IllegalArgumentException("채팅방 참여 정보를 찾을 수 없음"));
       relationship.updateLastReadAt();
   }

   // 채팅방 유저 체크
   public List<ChatUserDto> getParticipants(Long chatId, Long userId) {
       // 요청 유효성 검사 (요청을 보낸 사람이 채팅방에 속했는지 검사, 만약 문제가 있다면 에러를 throw)
       validate(chatId, userId);
       // 1대1 채팅방이면 다른 한명이 나갔어도 보여주는게 로직상 맞는 것 같음
       // 그룹 채팅방이면 어차피 존재하는 유저만 나옴
       List<UserChatRelationship> relationships = relationshipRepository.findAllByChatId(chatId);
       return relationships.stream()
               .map(r -> ChatUserDto.builder()
                       .userId(r.getUser().getId())
                       .nickname(r.getUser().getNickname())
                       .profileImageUrl(r.getUser().getProfileImgUrl())
                       .build())
               .toList();
   }

   // 유효한 접근인지 체크
   private void validate(Long chatId, Long userId) {
       relationshipRepository.findByUserIdAndChatId(userId, chatId)
               .orElseThrow(() -> new AccessDeniedException("해당 채팅방에 접근할 권한이 없습니다."));
   }
}
