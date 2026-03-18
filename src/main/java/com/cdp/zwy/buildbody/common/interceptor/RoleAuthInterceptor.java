package com.cdp.zwy.buildbody.common.interceptor;

import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWT;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 角色权限拦截器
 *
 * @author zwy
 * @version 1.0
 * @description: RoleAuthInterceptor
 * @date 2026/3/18
 */
@Component
public class RoleAuthInterceptor implements HandlerInterceptor {

    private static final byte[] JWT_KEY = "buildbody_secret_key_2026".getBytes(StandardCharsets.UTF_8);

    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        
        if (requireRole == null) {
            return true;
        }

        if (!requireRole.requireLogin()) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或token无效\",\"data\":null}");
            return false;
        }

        try {
            String jwtToken = token.substring(7);
            if (!JWTUtil.verify(jwtToken, JWT_KEY)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"token验证失败\",\"data\":null}");
                return false;
            }

            JWT jwt = JWTUtil.parseToken(jwtToken);
            List<String> userRoles = (List<String>) jwt.getPayload("roles");
            Long userId = Long.valueOf(jwt.getPayload("userId").toString());

            String[] requiredRoles = requireRole.value();
            
            if (requiredRoles.length == 0) {
                request.setAttribute("userId", userId);
                request.setAttribute("roles", userRoles);
                return true;
            }

            boolean hasPermission = Arrays.stream(requiredRoles).anyMatch(requiredRole -> 
                userRoles.contains(requiredRole)
            );

            if (!hasPermission) {
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"msg\":\"权限不足\",\"data\":null}");
                return false;
            }

            request.setAttribute("userId", userId);
            request.setAttribute("roles", userRoles);
            return true;

        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"token解析失败\",\"data\":null}");
            return false;
        }
    }
}