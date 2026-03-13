package com.example.mamatolmi.domain.auth.handler;

import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.repository.UserRepository;
import com.example.mamatolmi.global.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuthUser = (OAuth2User) authentication.getPrincipal();

        String email = oAuthUser.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        String token = jwtProvider.createToken(user.getId());

        String redirectUrl =
                (String) request.getSession().getAttribute("redirect_uri");

        if (redirectUrl == null) {
            redirectUrl = "https://mamatolmiscreen.vercel.app/oauth-success";
        }

        request.getSession().removeAttribute("redirect_uri");

        response.sendRedirect(redirectUrl + "?token=" + token);

//        String redirectUrl;
//
//        if (request.getServerName().contains("localhost")) {
//            redirectUrl = "http://localhost:5173/oauth-success";
//        } else {
//            redirectUrl = "https://mamatolmiscreen.vercel.app/oauth-success";
//        }
//
//        response.sendRedirect(redirectUrl + "?token=" + token);

        //response.sendRedirect("http://mamatolmiscreen.vercel.app/oauth-success?token=" + token);
    }
}
