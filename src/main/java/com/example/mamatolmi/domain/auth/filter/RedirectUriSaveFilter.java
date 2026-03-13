package com.example.mamatolmi.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RedirectUriSaveFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String redirectUri = request.getParameter("redirect_uri");

        System.out.println("=== RedirectUriSaveFilter ===");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("redirect_uri param: " + redirectUri);

        if (redirectUri != null) {
            request.getSession().setAttribute("redirect_uri", redirectUri);
            System.out.println("세션에 저장됨: " + redirectUri);
        }

        filterChain.doFilter(request, response);
    }
}
