package com.example.muscle_market.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class AlanChatPageController {

    @GetMapping("/alan-chat")
    public String alanChatPage(Model model){
        model.addAttribute("message", new ArrayList<>());
        return "alanchat";
    }
}
