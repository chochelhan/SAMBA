package com.inysoft.samba.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtil {

    private final String SECRET = "inysoftsecretriverflowsderasdederrweweweew";

    public HashMap<String,Object>  generateToken(UserDetails userDetails) {
        Claims claims = Jwts.claims();
        claims.put("username", userDetails.getUsername());
        return createToken(claims); // username을 subject로 해서 token 생성
    }


    private HashMap<String,Object> createToken(Claims claims) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        Date now = new Date(System.currentTimeMillis());
        Date tokenExpired = new Date(System.currentTimeMillis() + (1000 * 3600 * 24 * 2)); // 2일
        HashMap<String,Object> result = new HashMap<>();
        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(tokenExpired) // set Expire Time
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();

        result.put("accessToken",accessToken);
        result.put("tokenExpired",tokenExpired);
        return result;

    }

    /**
     * 토큰 유효여부 확인
     */
    public Boolean isValidToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        String userToken = userDetails.getPassword();


        //return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        return (username.equals(userDetails.getUsername()) && token.equals(userToken)); //기간 체크 안함
    }

    /**
     * 토큰의 Claim 디코딩
     */
    private Claims getAllClaims(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Claim 에서 username 가져오기
     */
    public String getUsernameFromToken(String token) {
        String username = String.valueOf(getAllClaims(token).get("username"));
        return username;
    }

    /**
     * 토큰 만료기한 가져오기
     */
    public Date getExpirationDate(String token) {
        Claims claims = getAllClaims(token);
        return claims.getExpiration();
    }

    /**
     * 토큰이 만료되었는지
     */
    private boolean isTokenExpired(String token) {

        return getExpirationDate(token).before(new Date(System.currentTimeMillis()));
    }
}
