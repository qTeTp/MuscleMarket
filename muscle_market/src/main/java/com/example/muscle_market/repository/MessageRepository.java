package com.example.muscle_market.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.muscle_market.domain.Message;
import com.example.muscle_market.dto.ChatMessageResponse;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
   // 채팅방 메시지 전체 조회 시 추후 페이지네이션 적용 필요한 쿼리
   @Query("SELECT new com.example.muscle_market.dto.ChatMessageResponse(" +
          "    m.chat.chatId, " +
          "    new com.example.muscle_market.dto.SimplifiedUserDto(" +
          "        m.sender.id, m.sender.nickname, m.sender.profileImgUrl" +
          "    ), " +
          "    m.content, " +
          "    m.createdAt" +
          ") " +
          "FROM Message m " +
          "WHERE m.chat.chatId = :chatId " +
          "ORDER BY m.createdAt ASC")
   List<ChatMessageResponse> findAllMessagesByChatId(@Param("chatId") Long chatId);

   // 여러 채팅방의 마지막 메시지 조회 (채팅방 리스트 보여줄 때 사용)
   @Query("SELECT m FROM Message m JOIN FETCH m.chat c " +
          "WHERE m.messageId IN (" +
          "    SELECT MAX(sub_m.messageId) FROM Message sub_m WHERE sub_m.chat.chatId IN :chatIds GROUP BY sub_m.chat.chatId" +
          ")")
   List<Message> findLastMessagesByChatIds(@Param("chatIds") List<Long> chatIds);

   // 채팅방의 모든 메시지 삭제
   @Modifying
   @Query("DELETE FROM Message m WHERE m.chat.chatId = :chatId")
   void deleteAllMessagesByChatId(@Param("chatId") Long chatId);

   // 특정 시간 이후 생성된 메시지 개수 세기
   @Query("SELECT count(m) FROM Message m " +
          "WHERE m.chat.chatId = :chatId AND m.createdAt > :lastReadAt")
   Long countUnreadMessagesAfter(@Param("chatId") Long chatId, @Param("lastReadAt") LocalDateTime lastReadAt);
}
