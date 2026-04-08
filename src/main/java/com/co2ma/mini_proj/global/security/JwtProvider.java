package com.co2ma.mini_proj.global.security;

import com.co2ma.mini_proj.member.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpireTime = 1000L * 60 * 30;
    private final long refreshTokenExpireTime = 1000L * 60 * 60 * 24 * 14;

    //secretKey에 jwt.secret에서 가져온 키가 매핑 됨
    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

     public String createAccessToken(String loginId, Role role) {
        return Jwts.builder() // 자동으로 header는 생성
                // payload에 들어가는 부분
                .subject(loginId) // id
                .claim("role", role.name()) // 추가정보 (여기서는 권한)
                .issuedAt(new Date()) // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpireTime)) // 만료 시간
                // 여기 까지 payload 부분
                .signWith(secretKey) //signature 부분
                .compact(); //Encode 하는 부분
     }

     public String createRefreshToken(String loginId){
        return Jwts.builder()
                .subject(loginId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
                .signWith(secretKey)
                .compact();
     }

     public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build() // secretkey를 통해 서명을 만들어 signature와 비교
                    .parseSignedClaims(token); // 자동으로 expiration 시간을 계산해줌, 초과시 Exception 발생
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
     }

     public String getLoginId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
     }

     public String getRole(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
     }


}
