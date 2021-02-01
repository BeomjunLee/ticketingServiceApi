package com.hoseo.hackathon.storeticketingservice.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoseo.hackathon.storeticketingservice.domain.dto.LoginSuccessDto;
import com.hoseo.hackathon.storeticketingservice.security.jwt.JwtFactory;
import com.hoseo.hackathon.storeticketingservice.security.tokens.PostAuthorizationToken;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
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

    private final MemberService memberService;

    /**
     * 로그인 성공시
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            PostAuthorizationToken token = (PostAuthorizationToken) authentication; //PostAuthorizationToken 으로 넘어온 인증 결과값에서

            //MemberContext 안에 있는 인증 결과 값 정보를 사용해 accessToken, refreshToken 발급
            String accessToken = jwtFactory.createAccessToken((String)token.getPrincipal(), token.getAuthorities());
            String refreshToken = jwtFactory.createRefreshToken((String)token.getPrincipal(), token.getAuthorities());

            //로그인 성공시 refreshToken 저장 및 갱신
            memberService.updateRefreshToken((String)token.getPrincipal(), refreshToken);

            //accessToken, refreshToken, accessToken 유효시간
            LoginSuccessDto loginSuccessDto = writeDTO(accessToken, refreshToken, jwtFactory.getAccessTokenExpiredTimeSeconds().intValue());

            //JSON 형태로 response
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), loginSuccessDto);
    }

    /**
     * 만들어진 jwt 토큰을 DTO 에 넣음
     */
    private LoginSuccessDto writeDTO(String accessToken, String refreshToken, int expireIn) {
        return new LoginSuccessDto("success", 200, "로그인 성공", "Bearer", accessToken, refreshToken, expireIn);
    }

}
