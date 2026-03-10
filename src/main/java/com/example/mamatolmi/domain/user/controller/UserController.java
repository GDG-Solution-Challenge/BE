package com.example.mamatolmi.domain.user.controller;

import com.example.mamatolmi.domain.user.dto.OnboardingRequestDTO;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @PatchMapping("/onboarding")
    public User onboarding(@RequestBody OnboardingRequestDTO request){

        return userService.updateUserSetting(
                request.getUserId(),
                request.getKoreanLevel(),
                request.getResponseLanguage()
        );
    }
}
