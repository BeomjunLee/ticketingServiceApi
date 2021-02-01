package com.hoseo.hackathon.storeticketingservice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hoseo.hackathon.storeticketingservice.security.MemberContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFactory {

    private static final Logger log = LoggerFactory.getLogger(JwtFactory.class);

    private final String SIGNING_KEY = "lbjlbjlbj";  //사이닝키
    private final String ISSUER = "leebeomjun";    //발행처

    /**
     * AccessToken
     */
    private final Long accessTokenExpiredTime = 1000 * 60L * 10L; //10분
    
    /**
     * RefreshToken
     */
    private final Long refreshTokenExpiredTime = 1000 * 60L * 60L * 24L * 14L; //2주

    /**
     * AccessToken 발급
     * @param username  사용자 아이디
     * @param authorities  사용자 권한
     * @return
     */
    public String createAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return generateToken(username, authorities, accessTokenExpiredTime);
    }

    /**
     * RefreshToken 발급
     * @param username  사용자 아이디
     * @param authorities   사용자 권한
     * @return
     */
    public String createRefreshToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return generateToken(username, authorities, refreshTokenExpiredTime);
    }

    /**
     * JWT 토큰 생성
     * @param username  사용자 아이디
     * @param authorities   사용자 권한
     * @param tokenExpiredTime  토큰 유효시간
     * @return
     */
    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities, Long tokenExpiredTime) {
        String token = null;
        Date ext = new Date();
        ext.setTime(ext.getTime() + tokenExpiredTime);
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
    
    /**
     *  Token 검증
     * @param token jwt 토큰
     */
    public MemberContext isValidToken(String token) {
        //요청한 토큰에 대해 유효성 검증 컨텍스트 생성
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SIGNING_KEY))
                    .build();

        //생성한 컨텍스르로 토큰을 검증하고 검증 실패시 Exception
        DecodedJWT jwt = verifier.verify(token);

        String username = jwt.getClaim("username").asString();  //JWT 토큰에서 username 추출
        String role = jwt.getClaim("role_").asList(String.class).get(0);    //JWT 토큰에서 role 추출

        return new MemberContext(username, "1234", role); //비밀번호는 아무거나 입력한것(필요없음)
    }

    /**
     * 암호화 방식
     * @return  암호화 방식
     */
    private Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(SIGNING_KEY);
    }

    /**
     * AccessToken 유효시간 초로 변환 (getter)
     * @return (seconds)
     */
    public Long getAccessTokenExpiredTimeSeconds() {
        return accessTokenExpiredTime / 1000;
    }
}
