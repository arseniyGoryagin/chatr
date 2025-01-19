package com.chatr.jwt;


import com.chatr.jwt.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl  implements JwtService{


    private final JwtConfig jwtConfig;


    private SecretKey getSecretKey(TokenType tokenType) {

        String secret = switch (tokenType){
            case ACCESS -> jwtConfig.getTokenSecret();
            case REFRESH -> jwtConfig.getTokenSecret();
            case RESETPASSWORD -> jwtConfig.getTokenSecret();
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
