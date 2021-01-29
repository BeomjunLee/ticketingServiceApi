package com.hoseo.hackathon.storeticketingservice.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

//로그인 요청은 검증 안하기 위해
public class FilterSkipMatcher implements RequestMatcher {

    private OrRequestMatcher orRequestMatcher;
    private RequestMatcher processingMatcher;

    public FilterSkipMatcher(List<String> pathToSkip, String ProcessingPath) {
        this.orRequestMatcher = new OrRequestMatcher(pathToSkip.stream().map(p -> new AntPathRequestMatcher(p)).collect(Collectors.toList()));
        this.processingMatcher = new AntPathRequestMatcher(ProcessingPath);
    }
    
    @Override
    public boolean matches(HttpServletRequest request) {
        return !orRequestMatcher.matches(request) && processingMatcher.matches(request);
    }
}
