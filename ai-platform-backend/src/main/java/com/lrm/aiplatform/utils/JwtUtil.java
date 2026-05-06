package com.lrm.aiplatform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {//@Value作用：赋值->secret 从配置文件中读取d
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        Date now = new Date();
        return Jwts.builder()
                // 放入自定义载荷（用户信息）
                .claims(claims)
                // 设置JWT标准字段：subject（主题），这里用用户名作为主题
                .subject(username)
                // 设置JWT标准字段：issuedAt（签发时间）= 当前时间
                .issuedAt(now)
                // 设置JWT标准字段：expiration（过期时间）= 当前时间 + 配置的过期时长
                .expiration(new Date(now.getTime() + expiration))
                // 签名：用密钥对JWT进行加密签名（防止令牌被篡改）
                .signWith(key)
                // 压缩生成最终的JWT字符串（格式：Header.Payload.Signature）
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                // 1. 设置验证签名的密钥（必须和生成token时的密钥一致）
                .verifyWith(key)
                // 2. 构建JWT解析器实例
                .build()
                // 3. 解析并验证令牌（核心：自动校验签名+过期时间）
                .parseSignedClaims(token)
                // 4. 从解析结果中获取载荷（Claims）数据
                .getPayload();
    }

    /**
     * 验证JWT令牌是否合法有效
     * @param token 前端传递的令牌
     * @return true=有效合法，false=无效非法
     */
    public boolean validateToken(String token) {
        try {
            // 核心：调用解析方法，自动校验签名、过期时间
            parseToken(token);
            // 解析无异常 → 令牌有效
            return true;
        } catch (Exception e) {
            // 解析抛出异常 → 令牌无效
            return false;
        }
    }
}
