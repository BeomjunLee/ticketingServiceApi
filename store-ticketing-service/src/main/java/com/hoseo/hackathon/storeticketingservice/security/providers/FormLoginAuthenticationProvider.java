package com.hoseo.hackathon.storeticketingservice.security.providers;

import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.security.MemberContext;
import com.hoseo.hackathon.storeticketingservice.security.tokens.PostAuthorizationToken;
import com.hoseo.hackathon.storeticketingservice.security.tokens.PreAuthorizationToken;
import com.hoseo.hackathon.storeticketingservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class FormLoginAuthenticationProvider implements AuthenticationProvider {

    private final MemberService memberService;

    /**
     * [인증 객체 만들어주는 메서드]
     * 인증 시작 단계에서 PreAuthorizationToken 을 만들어주고 만들어진 Token을 Provider에 전달하는 방식으로 인증
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PreAuthorizationToken token = (PreAuthorizationToken) authentication;
        String username = token.getUsername();
        String password = token.getPassword();

        Member member = memberService.loginCheck(username, password);
        if (member != null) {
            return PostAuthorizationToken.setPostAuthorizationTokenFromMemberContext(MemberContext.setMemberContextFromMember(member));
        }
        throw new NoSuchElementException("인증 정보가 정확하지 않습니다.");
    }

    /**
     * [어떤 인증 객체를 서포트 할지]
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthorizationToken.class.isAssignableFrom(authentication);
    }

}
