package com.hoseo.hackathon.storeticketingservice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtFactory {

    private static final Logger log = LoggerFactory.getLogger(JwtFactory.class);
    protected static final String SIGNING_KEY = "hoseoHackathon";  //사이닝키
    public static String ISSUER = "leebeomjun";    //발행처

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        String token = null;
        Long expiredTime = 1000 * 60L * 60L; //60분
        Date ext = new Date();
        ext.setTime(ext.getTime() + expiredTime);
        System.out.println(ext);
        try {
            token = JWT.create()
                    .withIssuer(ISSUER)
                    .withClaim("username", username)
                    .withClaim("role_", authorities.stream().map(r -> r.getAuthority()).collect(Collectors.toList())) //유저의 권한정보도 토큰에 넣기
                    .withExpiresAt(ext)
                    .sign(generateAlgorithm());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return token;
    }

    //암호화 방식
    private Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(SIGNING_KEY);
    }

}
