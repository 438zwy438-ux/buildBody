package com.cdp.zwy.buildbody.common.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    
    private static final byte[] JWT_KEY = "buildbody_secret_key_2026".getBytes(StandardCharsets.UTF_8);
    
    private static final long DEFAULT_EXPIRE_TIME = 1 * 24 * 60 * 60 * 1000; // 1天

    public static String createToken(Map<String, Object> payload) {
        return createToken(payload, DEFAULT_EXPIRE_TIME);
    }

    public static String createToken(Map<String, Object> payload, long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);
        
        JWT jwt = JWT.create()
                .setKey(JWT_KEY)
                .setExpiresAt(expireDate)
                .setIssuedAt(now);
        
        payload.forEach(jwt::setPayload);
        
        return jwt.sign();
    }

    public static boolean verifyToken(String token) {
        try {
            return JWTUtil.verify(token, JWT_KEY);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isTokenExpired(String token) {
        try {
            JWTValidator.of(token).validateDate();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean validateToken(String token) {
        if (!verifyToken(token)) {
            return false;
        }
        if (isTokenExpired(token)) {
            return false;
        }
        return true;
    }

    public static JWT parseToken(String token) {
        return JWTUtil.parseToken(token);
    }

    public static Object getPayload(String token, String key) {
        JWT jwt = parseToken(token);
        return jwt.getPayload(key);
    }

    public static Long getUserId(String token) {
        Object userId = getPayload(token, "userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
}