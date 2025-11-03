package com.example.muscle_market.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword; // 비밀번호 확인
    private String nickname;

}
