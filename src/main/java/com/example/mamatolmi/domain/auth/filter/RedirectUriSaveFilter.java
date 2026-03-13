package com.example.mamatolmi.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedirectUriSaveFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("===== RedirectUriSaveFilter START =====");
        log.info("Request URI : {}", request.getRequestURI());

        if (request.getRequestURI().equals("/oauth2/authorization/google")) {

            String redirectUri = request.getParameter("redirect_uri");

            log.info("redirect_uri param : {}", redirectUri);

            if (redirectUri != null) {
                request.getSession().setAttribute("redirect_uri", redirectUri);
                log.info("redirect_uri saved in session : {}", redirectUri);
            } else {
                log.info("redirect_uri not found");
            }
        }

        filterChain.doFilter(request, response);
    }
}
