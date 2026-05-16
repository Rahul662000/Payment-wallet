package com.rahul.user_service.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResopnse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timeStamo;
}
