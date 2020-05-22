package com.example.AuthService.security;

public class SecurityConstants {
    public static final String SECRET ="TajnaPoruka";
    public static final String TOKEN_BEARER_PREFIX= "Bearer ";
    public static final String HEADER_BEARER_TOKEN = "Auth";
    public static final long EXPIRE = 6000_000;

    public static final long ACCESS_TOKEN_EXPIRE = 10000;
}
