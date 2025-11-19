package com.example.muscle_market.dto;

import com.example.muscle_market.enums.BungaeStatus;

public record BungaeDetails(
    String bungaeLocation,
    Integer maxParticipants,
    Integer curParticipants,
    String bungaeDatetime,
    BungaeStatus bungaeStatus
) {
    public BungaeDetails(
        String bungaeLocation,
        Integer maxParticipants,
        Integer curParticipants,
        String bungaeDatetime,
        BungaeStatus bungaeStatus) {
            this.bungaeLocation = bungaeLocation;
            this.maxParticipants = maxParticipants;
            this.curParticipants = curParticipants;
            this.bungaeDatetime = bungaeDatetime;
            this.bungaeStatus = bungaeStatus;
    }
}
