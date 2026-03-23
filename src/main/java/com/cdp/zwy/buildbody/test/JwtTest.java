package com.cdp.zwy.buildbody.test;

import com.cdp.zwy.buildbody.common.utils.JwtUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testCreateToken() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("roles", java.util.List.of("ADMIN"));
        
        String token = JwtUtils.createToken(payload);
        System.out.println("生成的Token: " + token);
        
        Long userId = JwtUtils.getUserId(token);
        System.out.println("用户ID: " + userId);
    }

    @Test
    public void testTokenValidation() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("roles", java.util.List.of("ADMIN"));
        
        String token = JwtUtils.createToken(payload);
        
        System.out.println("Token验证: " + JwtUtils.validateToken(token));
        System.out.println("Token是否过期: " + JwtUtils.isTokenExpired(token));
    }

    @Test
    public void testExpiredToken() throws InterruptedException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("roles", java.util.List.of("ADMIN"));
        
        String token = JwtUtils.createToken(payload, 1000); // 1秒过期
        
        System.out.println("立即验证: " + JwtUtils.validateToken(token));
        
        Thread.sleep(1500); // 等待1.5秒
        
        System.out.println("1.5秒后验证: " + JwtUtils.validateToken(token));
        System.out.println("Token是否过期: " + JwtUtils.isTokenExpired(token));
    }
}