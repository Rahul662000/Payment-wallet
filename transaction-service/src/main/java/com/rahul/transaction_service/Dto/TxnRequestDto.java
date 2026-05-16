package com.rahul.transaction_service.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TxnRequestDto {
    @NotNull(message = "From user required")
    private Long fromUserId;

    @NotNull(message = "To user required")
    private Long toUserId;

    @NotNull(message = "Amount required")
    private Double amount;

    private String comment;
}
