package com.example.muscle_market.config;

import java.util.Objects;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.muscle_market.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        // STOMP 'CONNECT' 커맨드일 때만 인증처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 클라이언트가 보낸 'Authorization' 헤더 찾기
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // JWT 검증
                if (jwtUtil.validateToken(token)) {
                    // JwtUtil로 유저 이름 추출
                    String username = jwtUtil.extractUsername(token);
                    
                    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 인증 정보를 STOMP 세션의 'USER'로 등록
                    accessor.setUser(authToken);

                    log.info("STOMP User connected: {}", userDetails.getUsername());
                } else {
                    log.warn("STOMP: Invalid JWT token received");
                }
            } else {
                log.warn("STOMP: Missing Authorization header");
            }
        }

        return message;
    }
}
