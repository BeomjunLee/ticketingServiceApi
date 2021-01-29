package com.hoseo.hackathon.storeticketingservice.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoseo.hackathon.storeticketingservice.domain.dto.LoginDto;
import com.hoseo.hackathon.storeticketingservice.security.jwt.JwtFactory;
import com.hoseo.hackathon.storeticketingservice.security.tokens.PostAuthorizationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtFactory jwtFactory;

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            PostAuthorizationToken token = (PostAuthorizationToken) authentication; //PostAuthorizationToken으로 넘어온 인증 결과값에서
            String generateToken = jwtFactory.generateToken((String)token.getPrincipal(), token.getAuthorities());   //MemberContext안에 있는 인증 결과 값 정보를 사용해 JWT 토큰 생성
            LoginDto loginDto = writeDTO(generateToken);//DTO에도 토큰 값 넣어주기

            //JSON형태로 response
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), loginDto);
    }

    /**
     * 만들어진 jwt토큰을 DTO에 넣음
     */
    private LoginDto writeDTO(String token) {
        String result = "success";  //result에 성공값 넣기
        int status = 200;
        String message = "로그인 성공";
        return new LoginDto(result, status, message, token);
    }

}
