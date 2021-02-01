package com.hoseo.hackathon.storeticketingservice.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoseo.hackathon.storeticketingservice.domain.dto.LoginFailDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.LoginSuccessDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FormLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Logger log = LoggerFactory.getLogger("로그인 오류");
        log.error(exception.getMessage());
        LoginFailDto loginFailDto = writeDTO(exception.getMessage());

        //JSON형태로 response
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), loginFailDto);
    }

    private LoginFailDto writeDTO(String message) {
        return new LoginFailDto("fail", 401, message);
    }
}
