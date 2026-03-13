package com.example.mamatolmi.domain.auth.config;

import com.example.mamatolmi.domain.auth.filter.RedirectUriSaveFilter;
import com.example.mamatolmi.domain.auth.handler.AuthSuccessHandler;
import com.example.mamatolmi.domain.auth.service.AuthService;
import com.example.mamatolmi.global.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthService authService;
    private final AuthSuccessHandler authHandler;
    private final JwtFilter jwtFilter;
    private final RedirectUriSaveFilter redirectUriSaveFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(authService)
                        )
                        .successHandler(authHandler)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(redirectUriSaveFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}
