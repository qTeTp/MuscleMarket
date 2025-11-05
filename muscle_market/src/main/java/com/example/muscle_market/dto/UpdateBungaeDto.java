package com.example.muscle_market.dto;

import com.example.muscle_market.enums.BungaeStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateBungaeDto {
    private String location;
    private Integer maxParticipants;
    private Integer curParticipants;
    private BungaeStatus status;
    private String bungaeDate;

    @Builder
    public UpdateBungaeDto(String location, Integer maxParticipants, Integer curParticipants, BungaeStatus status, String bungaeDate) {
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.curParticipants = curParticipants;
        this.status = status;
        this.bungaeDate = bungaeDate;
    }
}
