package com.hoseo.hackathon.storeticketingservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessDto {
    private String result;
    private int status;
    private String message;
    private String token_type;
    private String access_token;
    private String refresh_token;
    private int expire_in;
}
