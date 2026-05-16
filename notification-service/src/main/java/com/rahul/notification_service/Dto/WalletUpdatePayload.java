package com.rahul.notification_service.Dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WalletUpdatePayload {

    private String userEmail;
    private Double balance;
    private String requestId;
}
