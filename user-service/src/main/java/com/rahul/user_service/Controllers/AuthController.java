package com.rahul.user_service.Controllers;

import com.rahul.user_service.Dtos.*;
import com.rahul.user_service.Service.AuthService;
import com.rahul.user_service.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final ModelMapper model;

    @PostMapping("/signup")
    public ResponseEntity<ApiResopnse<Long>> signup(@RequestBody SignUpRequest signUpRequest) throws ExecutionException, InterruptedException {
        log.info("SignUp Request Email = {}" , signUpRequest.getEmail());
        UserDto userDto = model.map(signUpRequest , UserDto.class);
        Long userId = userService.createUser(userDto);
        ApiResopnse<Long> response = ApiResopnse.<Long>builder()
                .success(true)
                .message("User Registered Successfully")
                .data(userId)
                .timeStamo(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResopnse<String>> login(@RequestBody LoginRequest loginRequest){

        AuthResponse authResponse = authService.login(loginRequest);
        ResponseCookie accessCookie = ResponseCookie.from(
                        "accessToken",
                        authResponse.getAccessToken()
                )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();


        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refreshToken",
                        authResponse.getRefreshToken()
                )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        ApiResopnse<String> response = ApiResopnse.<String>builder()
                .success(true)
                .message(authResponse.getMessage())
                .data(null)
                .timeStamo(LocalDateTime.now())
                .build();

        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(
                            HttpHeaders.SET_COOKIE,
                            accessCookie.toString()
                    );
                    headers.add(
                            HttpHeaders.SET_COOKIE,
                            refreshCookie.toString()
                    );
                })
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResopnse<String>> logout(@CookieValue("refreshToken") String refreshToken){
        String message = authService.logout(refreshToken);
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        ApiResopnse<String> response = ApiResopnse.<String>builder()
                .success(true)
                .message(message)
                .data(null)
                .timeStamo(LocalDateTime.now())
                .build();
        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(
                            HttpHeaders.SET_COOKIE,
                            accessCookie.toString()
                    );
                    headers.add(
                            HttpHeaders.SET_COOKIE,
                            refreshCookie.toString()
                    );
                })
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResopnse<String>> refresh( @CookieValue("refreshToken")  String refreshToken ) {
        AuthResponse authResponse = authService.refresh(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from(
                        "accessToken",
                        authResponse.getAccessToken()
                )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refreshToken",
                        authResponse.getRefreshToken()
                )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        ApiResopnse<String> response = ApiResopnse.<String>builder()
                .success(true)
                .message(authResponse.getMessage())
                .data(null)
                .timeStamo(LocalDateTime.now())
                .build();
        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(
                            HttpHeaders.SET_COOKIE,
                            accessCookie.toString()
                    );
                    headers.add(
                            HttpHeaders.SET_COOKIE,
                            refreshCookie.toString()
                    );
                })
                .body(response);
    }

}
