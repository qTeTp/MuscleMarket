package com.example.muscle_market.service;

import com.example.muscle_market.domain.Sport;
import com.example.muscle_market.domain.User;
import com.example.muscle_market.domain.UserFavoriteSport;
import com.example.muscle_market.dto.OnboardingDto;
import com.example.muscle_market.repository.SportRepository;
import com.example.muscle_market.repository.UserFavoriteSportRepository;
import com.example.muscle_market.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final UserRepository userRepository;
    private final SportRepository sportRepository;
    private final UserFavoriteSportRepository userFavoriteSportRepository;

    // 온보딩 추가 코드
    @Transactional
    public String completeOnboarding(OnboardingDto dto){
        // SecurityContext에서 username 추출
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // username으로 User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 입력된 운동명이 존재하는지 확인, 없으면 생성
        String sportName = dto.getSportName().trim();
        Sport sport = sportRepository.findByName(sportName)
                .orElseGet(() -> {
                    Sport newSport = Sport.builder()
                            .name(sportName)
                            .build();
                    return sportRepository.save(newSport);
                });

        // user_favorite_sports에 이미 존재하면 업데이트, 없으면 생성
        Optional<UserFavoriteSport> existinOpt = userFavoriteSportRepository.findByUser(user);

        if(existinOpt.isPresent()){
            UserFavoriteSport existing = existinOpt.get();
            existing.setSport(sport);
            existing.setSkillLevel(dto.getSkillLevel());
            userFavoriteSportRepository.save(existing);
        } else {
            UserFavoriteSport ufs = UserFavoriteSport.builder()
                    .user(user)
                    .sport(sport)
                    .skillLevel(dto.getSkillLevel())
                    .build();
            userFavoriteSportRepository.save(ufs);
        }

        // is_onboarded 업데이트
        user.setIsOnboarded(true);
        userRepository.save(user);

        return "온보딩 완료되었습니다.";
    }

    // 온보딩 수정 코드
    @Transactional
    public String updateFavoriteSport(OnboardingDto dto){
        // SecurityContext에서 username 추출하기
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // username으로 User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));


        // 클라이언트가 수정하면서 입력한 운동명이 존재하는지 확인, 없으면 생성하기
        String sportName = dto.getSportName().trim();
        Sport sport = sportRepository.findByName(sportName)
                .orElseGet(() -> {
                    Sport newSport = Sport.builder()
                            .name(sportName)
                            .build();
                    return sportRepository.save(newSport);
                });

        // 기존 선호 운동 조회하기
        UserFavoriteSport ufs = userFavoriteSportRepository
                .findByUser(user)
                .orElseThrow(() -> new RuntimeException("선호 운동 정보가 없습니다."));

        // 운동명/구력 수정후 업데이트
        ufs.setSport(sport);
        ufs.setSkillLevel(dto.getSkillLevel());
        userFavoriteSportRepository.save(ufs);

        return "선호 운동 정보가 수정되었습니다.";
    }



}
