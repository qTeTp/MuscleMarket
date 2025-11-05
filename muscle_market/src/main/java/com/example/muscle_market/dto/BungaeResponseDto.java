package com.example.muscle_market.dto;

import com.example.muscle_market.enums.BungaeStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BungaeResponseDto {
    private Long bungaeId;
    private String location;
    private Integer maxParticipants;
    private Integer curParticipants;
    private BungaeStatus status;
    private String bungaeDate;
    private Long hostId;

    @Builder
    public BungaeResponseDto(Long bungaeId, String location, Integer maxParticipants, Integer curParticipants, BungaeStatus status, String bungaeDate, Long hostId) {
        this.bungaeId = bungaeId;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.curParticipants = curParticipants;
        this.status = status;
        this.bungaeDate = bungaeDate;
        this.hostId = hostId;
    }
}
