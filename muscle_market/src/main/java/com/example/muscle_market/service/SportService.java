package com.example.muscle_market.service;

import com.example.muscle_market.domain.Sport;
import com.example.muscle_market.dto.SportDto;
import com.example.muscle_market.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SportService {
    private final SportRepository sportRepository;

    public List<SportDto> getAllSports() {
        List<Sport> sports = sportRepository.findAll();

        // dto 변환
        return sports.stream()
                .map(sport -> SportDto.builder()
                        .id(sport.getId())
                        .name(sport.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
