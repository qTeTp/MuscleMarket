package com.example.muscle_market.repository;

import com.example.muscle_market.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.muscle_market.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 디비에서 username으로 조회
    Optional<User> findByUsername(String username);
    // 디비에서 nickname으로 조회
    Optional<User> findByNickname(String nickname);
    // 디비에서 email으로 조회
    Optional<User> findByEmail(String email);
}
