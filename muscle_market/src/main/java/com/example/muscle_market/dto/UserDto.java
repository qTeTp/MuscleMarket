package com.example.muscle_market.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String confirmPassword; // 비밀번호 확인
    private String nickname;
}
