package com.example.muscle_market.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    // 실제 디비의 유저 엔티티
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }


    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getProfileImgUrl() {
        return user.getProfileImgUrl();
    }

    public Boolean getIsOnboarded() {
        return user.getIsOnboarded();
    }

    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return user.getUpdatedAt();
    }

    // Security에서 필요한 기본 메서드들
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // id, email 등 값이 필요하면 user.get~()으로 가져올 수 있음
}
