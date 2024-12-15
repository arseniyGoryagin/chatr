package com.chatr.util;


public class RequestParser {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    public static String getTokenFromHeader(String authHeader){
        return authHeader.substring(BEARER_PREFIX.length());
    }


}
