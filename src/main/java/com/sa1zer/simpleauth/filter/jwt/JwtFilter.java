package com.sa1zer.simpleauth.filter.jwt;

import com.sa1zer.simpleauth.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = jwtService.getTokenFromRequest(request);
        if(token.isPresent() && jwtService.isTokenValid(token.get())) {
            var authentication = (UsernamePasswordAuthenticationToken) jwtService.getAuthentication(token.get());

            if(authentication != null) {
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //установка дополнительной информации на основе запроса (ip, url, headers)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
