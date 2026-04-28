package com.lrm.aiplatform.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("test-secret-key-for-jwt-unit-testing-purposes", 3600000);
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        String token = jwtUtil.generateToken(1L, "admin", "teacher");
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void parseToken_shouldExtractClaims() {
        String token = jwtUtil.generateToken(1L, "admin", "teacher");
        Claims claims = jwtUtil.parseToken(token);
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("admin", claims.getSubject());
        assertEquals("teacher", claims.get("role", String.class));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(1L, "admin", "teacher");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_shouldReturnFalseForEmptyString() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void validateToken_shouldReturnFalseForNull() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() throws InterruptedException {
        JwtUtil shortLived = new JwtUtil("test-secret-key-for-jwt-unit-testing-purposes", 1);
        String token = shortLived.generateToken(1L, "admin", "teacher");
        Thread.sleep(10);
        assertFalse(shortLived.validateToken(token));
    }

    @Test
    void generateToken_differentUsersShouldHaveDifferentTokens() {
        String token1 = jwtUtil.generateToken(1L, "alice", "student");
        String token2 = jwtUtil.generateToken(2L, "bob", "teacher");
        assertNotEquals(token1, token2);
    }
}
