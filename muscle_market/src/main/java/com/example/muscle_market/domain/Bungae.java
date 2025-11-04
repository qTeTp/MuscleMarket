package com.example.muscle_market.domain;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.muscle_market.enums.BungaeStatus;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "bungaes")
@Getter
@NoArgsConstructor
@Entity
public class Bungae {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bungae_id", nullable = false)
    private Long bungaeId;

    @Setter
    @Column(name = "bungae_location", nullable = false)
    private String location;

    @Setter
    @Column(name = "bungae_cur_participants", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer curParticipants;

    @Setter
    @Column(name = "bungae_max_participants", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer maxParticipants;

    @Setter
    @Column(name = "bungae_status", nullable = false)
    private BungaeStatus status = BungaeStatus.OPENED;

    @Setter
    @Column(name = "bungae_datetime", nullable = false)
    private LocalDateTime bungaeDateTime;

    @Builder
    public Bungae(String location, Integer curParticipants, Integer maxParticipants, LocalDateTime bungaeDateTime) {
        this.location = location;
        this.curParticipants = curParticipants;
        this.maxParticipants = maxParticipants;
        this.bungaeDateTime = bungaeDateTime;
    }
}
