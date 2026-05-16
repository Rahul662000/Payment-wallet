package com.rahul.user_service.Dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;

@Setter
@Getter
@Builder
public class TokenReponse {
    private String accessToken;
    private String refreshToken;
}
