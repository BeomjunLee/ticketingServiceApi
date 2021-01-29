package com.hoseo.hackathon.storeticketingservice.security.tokens;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

//Jwt 인증 전 토큰값 담기위해
public class JwtPreProcessingToken extends UsernamePasswordAuthenticationToken {
    private JwtPreProcessingToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JwtPreProcessingToken(String token) {
        super(token, token.length());   //crendentials에는 그냥 token의 길이를 담아줬음
    }
}
