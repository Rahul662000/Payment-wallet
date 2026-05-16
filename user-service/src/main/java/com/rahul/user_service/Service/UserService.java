package com.rahul.user_service.Service;

import com.rahul.user_service.Dtos.LoginRequest;
import com.rahul.user_service.Dtos.UserCreatedPayload;
import com.rahul.user_service.Dtos.UserDto;
import com.rahul.user_service.Entity.User;
import com.rahul.user_service.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final  KafkaTemplate kafkaTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.created.topic}")
    private String userCreatedTopic;

    @Transactional
    public Long createUser(UserDto userDto) throws ExecutionException, InterruptedException {

        if(userRepo.findByEmail(userDto.getEmail()).isPresent()){
            throw new RuntimeException("User Already Exists");
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .phone(userDto.getPhone())
                .kycNumber(userDto.getKycNumber())
                .build();
        userRepo.save(user);

        UserCreatedPayload userCreatedPayload = UserCreatedPayload.builder()
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userName(user.getName())
                .requestId(MDC.get("requestId"))
                .build();

        Future<SendResult<String,Object>> future  = kafkaTemplate
                .send(userCreatedTopic, userCreatedPayload.getUserEmail(),userCreatedPayload);

        log.info("Pushed userCreatedPayload to kafka: {}",future.get());

        log.info(
                "User created userId={} email={}",
                user.getId(),
                user.getEmail()
        );

        return user.getId();

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
