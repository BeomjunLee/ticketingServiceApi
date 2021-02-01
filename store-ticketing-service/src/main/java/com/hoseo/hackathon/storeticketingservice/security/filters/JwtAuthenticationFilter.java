package com.hoseo.hackathon.storeticketingservice.security.filters;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.hoseo.hackathon.storeticketingservice.domain.dto.RefreshTokenDto;
import com.hoseo.hackathon.storeticketingservice.security.FilterSkipMatcher;
import com.hoseo.hackathon.storeticketingservice.security.HeaderTokenExtractor;

import com.hoseo.hackathon.storeticketingservice.security.tokens.JwtPreProcessingToken;

import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

    private HeaderTokenExtractor extractor;

    public JwtAuthenticationFilter(FilterSkipMatcher matcher, HeaderTokenExtractor extractor) {
        super(matcher);
        this.extractor = extractor;
    }

    /**
     *  토큰 인증 시도
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String header = request.getHeader("Authorization");   //Authorization 값의 헤더를 빼옴

        ObjectMapper objectMapper = new ObjectMapper();
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto();

        //만약에 클라이언트가 body에 grant_type:refresh_token와 header에 refresh_token값을 넣어서 보내면 인증후 access_token 재발급
        try {
             refreshTokenDto = objectMapper.readValue(request.getReader(), RefreshTokenDto.class);
        } catch (MismatchedInputException e) {  //refresh_token을 보내지 않을 때
            refreshTokenDto.setGrant_type("");
        }finally {
            String extractToken = extractor.extract(header);   //Authorization을 제외한 헤더 값에서 Bearer도 제외
            JwtPreProcessingToken token = new JwtPreProcessingToken(extractToken, refreshTokenDto.getGrant_type()); //Token, grant_type(refresh_type or "")
            return super.getAuthenticationManager().authenticate(token);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //컨텍스트를 만들어서 SecurityContextHolder 에 전달해서 보관
        SecurityContext context = SecurityContextHolder.createEmptyContext(); //새로운 컨텍스트 만들기
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context); //SecurityContextHolder 에 보관

        chain.doFilter(request, response); //모든 필터를 한번씩 돌게됨
    }

}
