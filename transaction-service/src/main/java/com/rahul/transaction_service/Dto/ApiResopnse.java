package com.rahul.transaction_service.Dto;

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
