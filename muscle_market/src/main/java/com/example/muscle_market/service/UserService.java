package com.example.muscle_market.service;

import com.example.muscle_market.config.JwtUtil;
import com.example.muscle_market.dto.LoginDto;
import com.example.muscle_market.dto.LoginResponseDto;
import com.example.muscle_market.dto.PostUserDto;
import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.muscle_market.domain.User;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
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
    public void login(LoginDto loginDto, HttpServletResponse response) {
        // 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        // Securitycontext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증 성공 시 Access 토큰, Refresh 토큰 발급
        String accessToken = jwtUtil.generateToken(loginDto.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(loginDto.getUsername());


        // SHA-256으로 해싱
//        String hashedRefresh = hashToken(refreshToken);

        // Refresh 토큰 발급 후 user 엔티티에 저장
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 쿠키로 발급 (HttpOnly 방식)
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .path("/")
//                .secure(true) // HTTPS 연결에서만 전송
//                .sameSite("Strict")   // 다른 사이트에서 요청시 쿠키 자동전송 방지
                .maxAge(60*15)  // 15분
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
//                .secure(true) // HTTPS 연결에서만 전송
//                .sameSite("Strict")   // 다른 사이트에서 요칭시 쿠키 자동전송 방지
                .maxAge(60 * 60 * 24 * 7)   // 7일
                .build();

        response.addHeader("set-Cookie", accessCookie.toString());
        response.addHeader("set-Cookie", refreshCookie.toString());
    }


    // OAuth2 로그인 처리
    public String[] oauthLogin(String email, String name) {
        // 기존 유저 조회, 없으면 신규 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email); // 이메일을 username으로 사용
                    newUser.setNickname(name);
                    newUser.setPassword(""); // 소셜 로그인은 비밀번호 없음
                    newUser.setIsOnboarded(false);
                    return userRepository.save(newUser);
                });

        // JWT 토큰 발급
        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Refresh 토큰 DB에 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 쿠키 생성은 핸들러에서 진행 (순환참조 때문에)
        return new String[]{accessToken, refreshToken};
    }


//    // SHA-256으로 refreshToken 해싱
//    private String hashToken(String token){
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            return Base64.getEncoder().encodeToString(digest.digest(token.getBytes()));
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }

    // oauth 사용자를 위한 이메일로 유저 조회
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    // 로그아웃
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            System.out.println("securityContextHolder에 인증정보없음");
        } else {
            System.out.println("SecurityContextHolder에 있는 username : " + authentication.getName());
        }

        String username = (authentication != null) ? authentication.getName() : null;

        if (username != null) {
            userRepository.findByUsername(username).ifPresent(user -> {
                user.setRefreshToken(null);
                userRepository.save(user);
            });
        }
    }

    // 현재 로그인한 유저 정보 조회
    public PostUserDto getCurrentUser(Long userId) {
        User curUser = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("유저 정보를 찾을 수 없습니다."));
        
        return PostUserDto.builder()
            .authorId(curUser.getId())
            .authorUsername(curUser.getUsername())
            .authorNickname(curUser.getNickname())
            .authorProfileImgUrl(curUser.getProfileImgUrl())
            .build();
    }
}
