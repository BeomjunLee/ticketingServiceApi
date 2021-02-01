package com.hoseo.hackathon.storeticketingservice.security.providers;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoseo.hackathon.storeticketingservice.domain.dto.AccessTokenDto;
import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.exception.NewAccessTokenIssuedException;
import com.hoseo.hackathon.storeticketingservice.security.MemberContext;
import com.hoseo.hackathon.storeticketingservice.security.filters.JwtAuthenticationFilter;
import com.hoseo.hackathon.storeticketingservice.security.jwt.JwtFactory;
import com.hoseo.hackathon.storeticketingservice.security.tokens.JwtPreProcessingToken;
import com.hoseo.hackathon.storeticketingservice.security.tokens.PostAuthorizationToken;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    private final JwtFactory jwtFactory;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    /**
     *  토큰 검증
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        String grantType = (String) authentication.getCredentials();

        //응답주기위해 HttpServletResponse 객체 생성
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getResponse();

            MemberContext member = null;
        try{
            member = jwtFactory.isValidToken(token);    //토큰 유효성 검사
            
            //클라이언트가 access_token이 만료됐다고 판단하여 refresh_token을 보냈을때 (grant_type = refresh_token 값 일때)
            if (grantType.equals("refresh_token")) {
                if (memberService.checkRefreshToken(member.getUsername(), token)) { //요청 refresh_token과  회원의 DB에 저장된 refresh_token 값이 일치할때
                    
                    //accessToken 발급
                    String accessToken = jwtFactory.createAccessToken(member.getUsername(), member.getAuthorities());
                    sendAccessToken(accessToken, jwtFactory.getAccessTokenExpiredTimeSeconds().intValue(), response);

                    throw new NewAccessTokenIssuedException("새로 access_token이 발급되었습니다");
                }
            }

        }
        catch (TokenExpiredException e) {
            log.error(e.getMessage());
            sendResponse("유효시간이 만료된 토큰입니다", response);
            throw new TokenExpiredException("유효시간이 만료된 토큰입니다");

        }catch (JWTVerificationException e) {
            log.error(e.getMessage());
            sendResponse("유효하지 않은 토큰입니다", response);
            throw new JWTVerificationException("유효하지 않은 토큰입니다");
        }
        return PostAuthorizationToken.setPostAuthorizationTokenFromMemberContext(member);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication); //Jwt 인증전 토큰을 서포트 해야됨
    }

    /**
     * 토큰 검증 예외처리
     * @param message error 메시지
     * @param res JSON 응답 만들 HttpServletResponse
     */
    private void sendResponse(String message, HttpServletResponse res){
        Response response =  Response.builder()
                .result("fail")
                .status(400)
                .message(message)
                .build();
        parseJson(response, res);
    }

    /**
     * JSON 형태로 accessToken 전송
     * @param accessToken accessToken
     * @param expireIn  만료시간
     */
    private void sendAccessToken(String accessToken, int expireIn, HttpServletResponse res) {
        AccessTokenDto accessTokenDto = AccessTokenDto.builder()
                .result("success")
                .status(200)
                .message("access_token 발급 성공")
                .token_type("Bearer")
                .access_token(accessToken)
                .expire_in(expireIn)
                .build();
        parseJson(accessTokenDto, res);
    }


    /**
     * JSON 으로 변환
     */
    private void parseJson(Object response, HttpServletResponse res) {
        //JSON 형태로 response
        res.setStatus(HttpStatus.OK.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        try {
            objectMapper.writeValue(res.getWriter(), response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
