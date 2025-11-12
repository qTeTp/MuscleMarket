package com.example.muscle_market.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @Value("${kakao.map.api-key}")
    private String kakaoMapApiKey;

    @GetMapping("/api/map")
    public String mapPage(Model model){
        model.addAttribute("kakaoMapApiKey",kakaoMapApiKey);
        return "map";
    }
}
