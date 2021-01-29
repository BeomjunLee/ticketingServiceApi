package com.hoseo.hackathon.storeticketingservice.security.filters;

import com.hoseo.hackathon.storeticketingservice.security.HeaderTokenExtractor;
import com.hoseo.hackathon.storeticketingservice.security.handlers.JwtAuthenticationFailureHandler;
import com.hoseo.hackathon.storeticketingservice.security.tokens.JwtPreProcessingToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private JwtAuthenticationFailureHandler failureHandler;
    private HeaderTokenExtractor extractor;

    protected JwtAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    public JwtAuthenticationFilter(RequestMatcher matcher, JwtAuthenticationFailureHandler failureHandler, HeaderTokenExtractor extractor) {
        super(matcher);
        this.failureHandler = failureHandler;
        this.extractor = extractor;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String tokenPayload = request.getHeader("Authorization");   //Authorization값의 헤더를 빼옴
        String extractToken = extractor.extract(tokenPayload);
        JwtPreProcessingToken token = new JwtPreProcessingToken(extractToken);
        return super.getAuthenticationManager().authenticate(token);
    }

    @Override //FilterChain은 이 요청에 대해서 작동해야할 필터들의 묶음
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //컨텍스트를 만들어서 SecurityContextHolder에 전달해서 보관
        SecurityContext context = SecurityContextHolder.createEmptyContext(); //새로운 컨텍스트 만들기
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context); //SecurityContextHolder에 보관

        chain.doFilter(request, response); //모든 필터를 한번씩 돌게됨
    }

    /**
     * 토큰 값이 정확하지않을떄
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext(); //인증받은 모든 컨텍스트가 사라짐
        this.failureHandler.onAuthenticationFailure(request, response, failed);
    }


}
