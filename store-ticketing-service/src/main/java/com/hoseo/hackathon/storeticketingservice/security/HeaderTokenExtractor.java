package com.hoseo.hackathon.storeticketingservice.security;

import com.hoseo.hackathon.storeticketingservice.security.exception.InvalidJwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

//헤더에 있는 토큰 추출 클래스
@Component
public class HeaderTokenExtractor {

    private static final String HEADER_PREFIX = "Bearer ";  //접두사

    public String extract(String header) {

        //들어온 토큰이 정보가 문제가 있을경우
        if (!StringUtils.hasText(header) || header.length() < HEADER_PREFIX.length()) { //헤더가 없거나 헤더의 길이가 HEADER_PREFIX보다 짧은경우
            throw new InvalidJwtException("올바른 토큰 정보가 아닙니다");
        }
        String token = header.substring(HEADER_PREFIX.length(), header.length());
        return token; //Bearer의 끝부터 시작해서 header의 끝까지 떼어서 가져감
    }
}
