package com.example.muscle_market.controller.api;

import com.example.muscle_market.dto.UserDto;
import com.example.muscle_market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody UserDto userDto){
        userService.singUp(userDto);
        return "회원가입 성공";
    }

}
