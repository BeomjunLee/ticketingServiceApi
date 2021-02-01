package com.hoseo.hackathon.storeticketingservice.security.tokens;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

//Jwt 인증 전 토큰값 담기위해
public class JwtPreProcessingToken extends UsernamePasswordAuthenticationToken {
    private JwtPreProcessingToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JwtPreProcessingToken(String token, String grantType) {
        super(token, grantType);   //token, grant_type(refresh_token 을 발급받지 않을 때는 "")
    }
}
