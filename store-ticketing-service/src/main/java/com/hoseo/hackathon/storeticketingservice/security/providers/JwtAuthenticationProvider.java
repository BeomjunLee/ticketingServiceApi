package com.hoseo.hackathon.storeticketingservice.security.providers;
import com.hoseo.hackathon.storeticketingservice.security.MemberContext;
import com.hoseo.hackathon.storeticketingservice.security.jwt.JwtDecoder;
import com.hoseo.hackathon.storeticketingservice.security.tokens.JwtPreProcessingToken;
import com.hoseo.hackathon.storeticketingservice.security.tokens.PostAuthorizationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        MemberContext context = jwtDecoder.decodeJwt(token);
        return PostAuthorizationToken.setPostAuthorizationTokenFromMemberContext(context);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication); //Jwt 인증전 토큰을 서포트 해야됨
    }
}
