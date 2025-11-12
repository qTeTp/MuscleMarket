package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.SportDto;
import com.example.muscle_market.service.SportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    public List<SportDto> getSports() {
        return sportService.getAllSports();
    }
}