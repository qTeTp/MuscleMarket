package com.example.muscle_market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
//    private String refreshToken;    // refresh 토큰
    private String tokenType;   // 항상 Bearer
}
