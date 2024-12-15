package com.chatr.jwt;

public enum TokenType {

    REFRESH(7 * 24 * 60 * 60 * 1000),
    ACCESS(15 * 60 * 1000),
    RESETPASSWORD(15 * 60 * 1000);


    TokenType(long expirationTime){
        this.expirationTime = expirationTime;
    }

    long expirationTime;

}
