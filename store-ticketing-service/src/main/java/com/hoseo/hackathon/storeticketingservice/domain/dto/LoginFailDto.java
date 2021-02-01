package com.hoseo.hackathon.storeticketingservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFailDto {
    private String result;
    private int status;
    private String message;
}
