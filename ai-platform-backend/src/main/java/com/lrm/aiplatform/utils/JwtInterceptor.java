package com.lrm.aiplatform.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 * 作用：在每个请求到达 Controller 之前，检查是否带了有效 token。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);

        if (token == null || !jwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或token已过期\",\"data\":null}");
            return false;
        }

        Claims claims = jwtUtil.parseToken(token);
        request.setAttribute("userId", claims.get("userId", Long.class));
        request.setAttribute("username", claims.getSubject());
        request.setAttribute("role", claims.get("role", String.class));

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        String customToken = request.getHeader("token");
        if (customToken != null && !customToken.isEmpty()) {
            return customToken;
        }
        return null;
    }
}
