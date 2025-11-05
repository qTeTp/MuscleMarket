package com.example.muscle_market.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

//    // 랜덤 비밀키
//    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    // 토큰 만료 시간(24시간)
//    private final long expiration = 1000 * 60 * 60 * 24;

    // JWT 시크릿키 랜덤이 아닌 환경변수로 고정으로 들어가게 변경
    private final Key key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());   // HS256용 key 생성
        this.expiration = expiration;
    }

    // JWT 생성
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    // JWT 검증 및 username 추출
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 유효성 체크
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }
}
