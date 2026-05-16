package com.rahul.notification_service.Dto;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserCreatedPayload implements Serializable {

    private static final long serialVersionUID = 1l;

    private Long userId;
    private String userName;
    private String userEmail;
    private String requestId;
}

