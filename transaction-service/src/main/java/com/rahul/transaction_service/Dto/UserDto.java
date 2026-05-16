package com.rahul.transaction_service.Dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

@Setter
@Getter
@ToString
public class UserDto {
    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String phone;

    @NotNull
    private String kycNumber;
}
