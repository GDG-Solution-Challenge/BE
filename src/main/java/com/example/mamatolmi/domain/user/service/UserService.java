package com.example.mamatolmi.domain.user.service;

import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.enums.KoreanLevelType;
import com.example.mamatolmi.domain.user.enums.ResponseLanguageType;
import com.example.mamatolmi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User loginOrCreateUser(String name, String email){
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        new User(name, email, null, null)
                ));
    }

    @Transactional
    public User updateUserSetting(Long userId,
                                  KoreanLevelType level,
                                  ResponseLanguageType languageType){
        User user = userRepository.findById(userId)
                .orElseThrow();

        user.updateSetting(level, languageType);

        return user;
    }

    public User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow();
    }
}
