package com.example.muscle_market.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.muscle_market.domain.Chat;
import com.example.muscle_market.domain.RelationshipStatus;
import com.example.muscle_market.domain.UserChatRelationship;

@Repository
public interface UserChatRelationshipRepository extends JpaRepository<UserChatRelationship, Long> {
    // 채팅방 모든 관계 삭제
    @Modifying
    @Query("DELETE FROM UserChatRelationship ucr WHERE ucr.chat.chatId = :chatId")
    void deleteAllByChatId(@Param("chatId") Long chatId);

    // 사용자의 특정 채팅방 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "WHERE ucr.user.id = :userId " +
           "AND ucr.chat.chatId = :chatId")
    Optional<UserChatRelationship> findByUserIdAndChatId(@Param("userId") Long userId, @Param("chatId") Long chatId);

    // 채팅방의 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "JOIN FETCH ucr.user " +
           "WHERE ucr.chat.chatId = :chatId")
    List<UserChatRelationship> findAllByChatId(@Param("chatId") Long chatId);

    // 사용자의 "ACTIVE"한 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "JOIN FETCH ucr.chat c " +
           "WHERE ucr.user.id = :userId AND ucr.status = :status")
    List<UserChatRelationship> findAllByUserIdWithChat(@Param("userId") Long userId, @Param("status") RelationshipStatus status);

    // 한 채팅방의 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "JOIN FETCH ucr.user u " +
           "JOIN FETCH ucr.chat c " +
           "WHERE ucr.chat.chatId IN :chatIds")
    List<UserChatRelationship> findAllByChat_ChatIdIn(@Param("chatIds") List<Long> chatIds);

    // 1대1 채팅 한정, 둘 중 하나라도 ACTIVE한 채팅방 조회
    @Query("SELECT ucr1.chat " +
           "FROM UserChatRelationship ucr1 " +
           "JOIN UserChatRelationship ucr2 ON ucr1.chat.chatId = ucr2.chat.chatId " +
           "WHERE ucr1.user.id = :userId1 " +
           "AND ucr2.user.id = :userId2 " +
           "AND (ucr1.status = :status OR ucr2.status = :status) " +
           
           "AND ucr1.chat.chatId IN (" +
           "    SELECT c.chat.chatId FROM UserChatRelationship c " +
           "    GROUP BY c.chat.chatId " +
           "    HAVING COUNT(c.chat.chatId) = 2" +
           ")")
    Optional<Chat> findActiveOneToOneChatByUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2, @Param("status") RelationshipStatus status);
}
