package com.chatr.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Slf4j
public class JwtServiceImpl  implements JwtService{

    @Value("${token.secret}")
    private String accessTokenSecret;

    @Value("${token.secret}")
    private String refreshTokenSecret;

    @Value("${token.secret}")
    private String resetPasswordSecret;



    private SecretKey getSecretKey(TokenType tokenType) {

        String secret = switch (tokenType){
            case ACCESS -> accessTokenSecret;
            case REFRESH -> refreshTokenSecret;
            case RESETPASSWORD -> resetPasswordSecret;
            };

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    private boolean isTokenExpired(String token,TokenType tokenType){
        return getExpDate(token, tokenType).after(new Date());
    }

    private Date getExpDate(String token, TokenType tokenType){
        Claims claims = Jwts.parser()
                .setSigningKey(getSecretKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }



    @Override
    public String getUsername(String token, TokenType tokenType) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSecretKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    @Override
    public String generateToken(String username, TokenType tokenType) {

        long currentTimeMillis = System.currentTimeMillis();
        Date currentTime = new Date(currentTimeMillis);
        Date expTimeDate = new Date(currentTimeMillis + tokenType.expirationTime);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentTime)
                .expiration(expTimeDate)
                .signWith(getSecretKey(tokenType))
                .compact();
    }

    @Override
    public boolean validateToken(String token, TokenType tokenType) {
        return isTokenExpired(token, tokenType);
    }
}
