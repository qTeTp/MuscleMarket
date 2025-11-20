package com.example.muscle_market.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatViewController {
   @GetMapping
   public String getChatListView() {
       return "chatList";
   }

}

