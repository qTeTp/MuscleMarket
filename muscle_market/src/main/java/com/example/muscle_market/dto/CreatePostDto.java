package com.example.muscle_market.dto;

import java.util.List;

import com.example.muscle_market.enums.BungaeStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePostDto {
    private String title;
    private String content;
    private Long sportId;
    private Boolean isBungae;
    private String bungaeLocation;
    private Integer maxParticipants;
    private Integer curParticipants;
    private String bungaeDatetime;
    private List<String> postImages;

    // 번개 게시글이면 번개 정보 담아서 객체 리턴 (비즈니스 로직에 사용)
    public BungaeDetails toBungaeDetails() {
        return new BungaeDetails(bungaeLocation, maxParticipants, curParticipants, bungaeDatetime, BungaeStatus.OPENED);
    }
}
