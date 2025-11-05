<<<<<<< HEAD
//package com.example.muscle_market.controller.view;
//
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.example.muscle_market.domain.User;
//import com.example.muscle_market.dto.ChatUserDto;
//
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/chats")
//public class ChatViewController {
//    @GetMapping
//    public String getChatListView(Model model, @AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        ChatUserDto currentUser = ChatUserDto.builder()
//            .userId(user.getId())
//            .nickname(user.getNickname())
//            .profileImageUrl(user.getProfileImgUrl())
//            .build();
//
//        model.addAttribute("currentUser", currentUser);
//        return "/chat/chatList";
//    }
//
//}
=======
package com.example.muscle_market.controller.view;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.muscle_market.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.muscle_market.domain.User;
import com.example.muscle_market.dto.ChatUserDto;


@Controller
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatViewController {
    @GetMapping
    public String getChatListView(Model model, @AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        ChatUserDto currentUser = ChatUserDto.builder()
            .userId(user.getId())
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImgUrl())
            .build();
        
        model.addAttribute("currentUser", currentUser);
        return "/chat/chatList";
    }
    
}
>>>>>>> feat/product_edit
