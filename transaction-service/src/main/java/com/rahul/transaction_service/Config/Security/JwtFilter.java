package com.rahul.transaction_service.Config.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("accessToken")){
                    token = cookie.getValue();
                }
            }
        }

        if (token == null) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter()
                    .write("Token Missing");
            return;
        }

        if (!jwtService.validateToken(token)) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter()
                    .write("Invalid Token");
            return;
        }

        String email = jwtService.extractUserName(token);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email , null , Collections.emptyList()
        );

        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        MDC.put("user", email);
        log.info("Authenticated user={}", email);

        filterChain.doFilter(request, response);
        MDC.clear();


    }
}
