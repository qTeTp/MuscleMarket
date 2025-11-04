package com.example.muscle_market.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_chat_relationships")
@Getter
@Entity
@NoArgsConstructor
public class UserChatRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Setter
    @Enumerated(EnumType.ORDINAL)
    private RelationshipStatus status;

    private LocalDateTime lastReadAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Builder
    public UserChatRelationship(User user, Chat chat, RelationshipStatus status) {
        this.user = user;
        this.chat = chat;
        this.status = status;
        updateLastReadAt();
    }

    // update lastReadAt to current time
    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }

    // leave chat
    public void leaveChat() {
        this.status = RelationshipStatus.LEFT;
    }

    // rejoin chat
    public void rejoinChat() {
        this.status = RelationshipStatus.ACTIVE;
    }
}
