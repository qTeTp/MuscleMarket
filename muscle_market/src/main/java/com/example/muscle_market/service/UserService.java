package com.example.muscle_market.service;

import com.example.muscle_market.config.JwtUtil;
import com.example.muscle_market.dto.LoginDto;
import com.example.muscle_market.dto.LoginResponseDto;
import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.muscle_market.domain.User;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 이메일 정규식 패턴
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    // 회원가입 기능
    public User singUp(UserDto userDto){
        // 아이디 중복 체크
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()){

            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 중복 확인
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())){
            throw new RuntimeException("비밀번호 확인이 일치하지 않습니다.");
        }

        // 이메일 형식 확인
        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()){
            throw new RuntimeException("올바른 이메일 형식이 아닙니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.findByNickname(userDto.getNickname()).isPresent()){
            throw new RuntimeException("이미 존재하는 닉네임 입니다.");
        }

        // 이메일 중복 체크
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new RuntimeException("이미 존재하는 이메일 입니다.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setNickname(userDto.getNickname());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setIsOnboarded(false); // 초기값 false

        return userRepository.save(user);
    }

    // 로그인 기능
    public LoginResponseDto login(LoginDto loginDto){
        // 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        // 인증 성공 시 Access 토큰, Refresh 토큰 발급
        String accessToken = jwtUtil.generateToken(loginDto.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(loginDto.getUsername());

        // SHA-256으로 해싱
        String hashedRefreshToken = hashToken(refreshToken);

        // Refresh 토큰 발급 후 user 엔티티에 저장
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        user.setRefreshToken(hashedRefreshToken);
        userRepository.save(user);

        // JSON 형식으로 반환
        return new LoginResponseDto(accessToken, refreshToken, "Bearer");
    }

    // SHA-256으로 refreshToken 해싱
    private String hashToken(String token){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Refresh token 해싱 실패", e);
        }
    }
}
