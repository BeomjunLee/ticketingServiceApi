package com.hoseo.hackathon.storeticketingservice.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoseo.hackathon.storeticketingservice.domain.form.LoginForm;
import com.hoseo.hackathon.storeticketingservice.security.tokens.PreAuthorizationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FormLoginFilter extends AbstractAuthenticationProcessingFilter {

    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 성공 실패 판단
     */
    public FormLoginFilter(String defaultUrl, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        super(defaultUrl);
        this.authenticationSuccessHandler = successHandler;
        this.authenticationFailureHandler = failureHandler;
    }

    protected FormLoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    /**
     * [인증 시도] - Provider의 authenticate메서드를 호출해서 인증정보가 유효한지 검증
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        //요청 정보를 받은 DTO
        LoginForm loginFormDto = objectMapper.readValue(request.getReader(), LoginForm.class);

        PreAuthorizationToken token = new PreAuthorizationToken(loginFormDto);  //PreAuthorization 안에 LoginDTO 값을 세팅하고

        return super.getAuthenticationManager().authenticate(token);    
        //AuthenticationManager 로 FormLoginAuthenticationProvider 에서 오버라이드해서 사용한 authenticate 메서드를 통해 인증 진행
        //<<모든 방식은 AuthenticationManager 를 통해 접근해야됨>>
    }

    /**
     * [인증 성공] - jwt토큰 생성해서 HttpResponse로 내려줌
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }

    /**
     * [인증 실패]
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }
}
