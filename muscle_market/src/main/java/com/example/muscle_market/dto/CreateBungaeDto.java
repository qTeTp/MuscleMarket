package com.example.muscle_market.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateBungaeDto {
    private String location;
    private Integer maxParticipants;
    private Integer curParticipants;
    private String bungaeDate;

    @Builder
    public CreateBungaeDto(String location, Integer maxParticipants, Integer curParticipants, String bungaeDate) {
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.curParticipants = curParticipants;
        this.bungaeDate = bungaeDate;
    }
}
