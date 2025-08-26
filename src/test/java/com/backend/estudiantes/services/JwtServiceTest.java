package com.backend.estudiantes.services;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;

    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expirationMs = 86400000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = secretKey;
        jwtService.jwtExpirationMs = expirationMs;
    }

    @Test
    void generateToken_WithUserDetails_ReturnsValidToken() {
        UserDetails userDetails = User.withUsername("test@example.com")
                .password("password")
                .roles("ESTUDIANTE")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_FromValidToken_ReturnsUsername() {
        UserDetails userDetails = User.withUsername("test@example.com")
                .password("password")
                .roles("ESTUDIANTE")
                .build();

        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void isTokenValid_WithValidToken_ReturnsTrue() {
        UserDetails userDetails = User.withUsername("test@example.com")
                .password("password")
                .roles("ESTUDIANTE")
                .build();

        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }
}