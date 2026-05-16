package com.rahul.user_service.Controllers;

import com.rahul.user_service.Dtos.ApiResopnse;
import com.rahul.user_service.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResopnse<String>> currentUser(Authentication authentication){
        ApiResopnse<String> response = ApiResopnse.<String>builder()
                .success(true)
                .message("Current user fetched successfully")
                .data(authentication.getName())
                .timeStamo(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

}
