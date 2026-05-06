package com.rishi.MultimediaWebApp;

import com.rishi.MultimediaWebApp.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {

        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(
                jwtUtil,
                "secret",
                "thisIsAVeryLongSecretKeyForJwtTesting123456"
        );

        ReflectionTestUtils.setField(
                jwtUtil,
                "expiration",
                86400000L
        );
    }

    @Test
    void generateToken_notNull() {

        String token =
                jwtUtil.generateToken("rishi@test.com");

        assertNotNull(token);

        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_returnsCorrectEmail() {

        String token =
                jwtUtil.generateToken("rishi@test.com");

        String email =
                jwtUtil.extractEmail(token);

        assertEquals("rishi@test.com", email);
    }

    @Test
    void validateToken_validToken_returnsTrue() {

        String token =
                jwtUtil.generateToken("rishi@test.com");

        assertTrue(jwtUtil.isValid(token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {

        assertFalse(
                jwtUtil.isValid("invalid.token")
        );
    }

    @Test
    void validateToken_emptyToken_returnsFalse() {

        assertFalse(
                jwtUtil.isValid("")
        );
    }
}