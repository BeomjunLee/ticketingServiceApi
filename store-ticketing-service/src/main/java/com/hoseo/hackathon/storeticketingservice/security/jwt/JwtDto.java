package com.hoseo.hackathon.storeticketingservice.security.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class JwtDto {
    private String username;
    private String role;
    private String issuer;
    private Date expiresAt;
}
