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
            String token = null;

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                String cookieHeader = accessor.getFirstNativeHeader("cookie");
                if (cookieHeader != null) {
                    token = extractTokenFromCookieString(cookieHeader);
                }
            }

            if (token != null && jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);

                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                accessor.setUser(authToken);

                log.info("STOMP User Connected: {}", username);
            } else {
                log.warn("STOMP: Token is missing or invalid");
            }
        }

        return message;
    }

    private String extractTokenFromCookieString(String cookieHeader) {
        if (cookieHeader == null) return null;

        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=");
            if (parts.length == 2 && "accessToken".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }
}
