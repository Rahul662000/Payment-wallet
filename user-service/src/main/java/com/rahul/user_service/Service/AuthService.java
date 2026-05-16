package com.rahul.user_service.Service;

import com.rahul.user_service.Dtos.AuthResponse;
import com.rahul.user_service.Dtos.LoginRequest;
import com.rahul.user_service.Entity.User;
import com.rahul.user_service.Repo.UserRepo;
import com.rahul.user_service.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    private final TokenBlackListService tokenBlackListService;

    public AuthResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtils.GenerateAccessToken(user.getEmail());
        String refreshToken = jwtUtils.GenerateRefreshToken(user.getEmail());

        AuthResponse authResponse = new AuthResponse(accessToken , refreshToken , "Login Successful");

        return authResponse;
    }

    public String logout(String refreshToken) {
        tokenBlackListService.blackListToken(refreshToken);
        return "Logout Successfully";
    }

    public AuthResponse refresh(String refreshToken) {
        if(tokenBlackListService.isTokenBlackListed(refreshToken)){
            throw new RuntimeException("Refresh Token Is Not Valid");
        }
        if (!jwtUtils.isRefreshToken(refreshToken)) {
            throw new RuntimeException(
                    "Invalid Refresh Token"
            );
        }
        String email = jwtUtils.extractUserName(refreshToken);
        User user = userRepo.findByEmail(email).orElseThrow();
        if (!jwtUtils.validateToken( refreshToken, user)){
            throw new RuntimeException("Refresh Token Expired or Invalid");
        }
        String newAccessToken = jwtUtils.GenerateAccessToken(user.getEmail());
        tokenBlackListService.blackListToken(refreshToken);
        String newRefreshToken = jwtUtils.GenerateRefreshToken(user.getEmail());


        return new AuthResponse(newAccessToken , newRefreshToken , "Access Token Refreshed");
    }
}




