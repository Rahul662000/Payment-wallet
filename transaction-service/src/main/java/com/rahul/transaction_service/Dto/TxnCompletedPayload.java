package com.rahul.transaction_service.Dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TxnCompletedPayload {
    private Long id; // ID for actual txn
    private Boolean success;
    private String reason;
    private String requestId;

}