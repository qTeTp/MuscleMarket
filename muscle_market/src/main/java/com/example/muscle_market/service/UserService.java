package com.example.muscle_market.service;

import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.muscle_market.domain.User;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 이메일 정규식 패턴
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");


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
}
