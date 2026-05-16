package com.rahul.user_service.Filter;

import com.rahul.user_service.Service.TokenBlackListService;
import com.rahul.user_service.Service.UserService;
import com.rahul.user_service.Utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlackListService tokenBlackListService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
//            final String authHeader = request.getHeader("Authorization");
//
//            if(authHeader == null || !authHeader.startsWith("Bearer ")){
//                filterChain.doFilter(request , response);
//                return;
//            }
//
//            String token = authHeader.substring(7);


            String token = null;

            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        token = cookie.getValue();
                    }
                }
            }

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtUtils.isAccessToken(token)) {
                response.setContentType("application/json");
                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );
                response.getWriter()
                        .write(
                                """
                                {
                                  "success": false,
                                  "message": "Invalid token"
                                }
                                """
                        );
                return;
            }

            String  userName = jwtUtils.extractUserName(token);

            if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userService.loadUserByUsername(userName);
                if(jwtUtils.validateToken(token , userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    MDC.put("User",userName);
                    log.info("Authenticated User = {}", userName);
                }
            }
            filterChain.doFilter(request,response);
            MDC.clear();
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Token");
        }
    }
}
