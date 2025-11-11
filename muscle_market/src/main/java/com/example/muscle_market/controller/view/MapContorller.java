package com.example.muscle_market.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapContorller {

    @GetMapping("/map")
    public String mapPage(){
        return "map";
    }
}
