package com.rahul.user_service.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String kycNumber;

}
