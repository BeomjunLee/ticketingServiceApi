package com.hoseo.hackathon.storeticketingservice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hoseo.hackathon.storeticketingservice.domain.Member;
import com.hoseo.hackathon.storeticketingservice.security.MemberContext;
import com.hoseo.hackathon.storeticketingservice.security.exception.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class JwtDecoder {
    private static final Logger log = LoggerFactory.getLogger(JwtDecoder.class);

    /**
     * 검증해서 유효하면 유효한 MemberContext을 리턴
     * 유효하지않으면 예와나 비어있는 MemberContext리턴
     */
    public MemberContext decodeJwt(String token) {
        DecodedJWT decodedJWT = isValidToken(token).orElseThrow(() -> new InvalidJwtException("유요한 토큰이 아닙니다"));
        String username = decodedJWT.getClaim("username").asString();
        String role = decodedJWT.getClaim("role_").asList(String.class).get(0);
        String issuer = decodedJWT.getIssuer();
        Date expiresAt = decodedJWT.getExpiresAt();

        if(issuer.equals(JwtFactory.ISSUER)){//발행처 검사
            if(expiresAt.after(new Date())){//토큰 만료 시간 검사
                return new MemberContext(username, "1234", role); //비밀번호는 아무거나 입력한것(필요없음)
            }else{
                throw new InvalidJwtException("토큰 시간이 만료되었습니다. 다시 로그인 해주세요");
            }
        }else{
            throw new InvalidJwtException("저희 토큰정보가 아닙니다");
        }

    }
    
    //signingkey 검증
    private Optional<DecodedJWT> isValidToken(String token) {
        DecodedJWT jwt = null;
        String signingKey = JwtFactory.SIGNING_KEY;
        try {
            Algorithm algorithm = Algorithm.HMAC256(signingKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Optional.ofNullable(jwt);
    }
}
