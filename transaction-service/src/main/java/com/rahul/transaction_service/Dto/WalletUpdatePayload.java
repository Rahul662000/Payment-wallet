package com.rahul.transaction_service.Dto;

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

