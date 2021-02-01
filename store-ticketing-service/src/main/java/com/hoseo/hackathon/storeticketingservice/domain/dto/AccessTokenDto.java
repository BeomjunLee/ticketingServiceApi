package com.hoseo.hackathon.storeticketingservice.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessTokenDto {
    private String result;
    private int status;
    private String message;
    private String token_type;
    private String access_token;
    private int expire_in;
}
