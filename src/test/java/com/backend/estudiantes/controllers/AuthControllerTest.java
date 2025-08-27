package com.backend.estudiantes.controllers;

import com.backend.estudiantes.dto.LoginRequest;
import com.backend.estudiantes.models.Usuario;
import com.backend.estudiantes.services.AuthService;
import com.backend.estudiantes.services.JwtService;
import com.backend.estudiantes.models.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_WithValidCredentials_ReturnsToken() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setRol(Role.ESTUDIANTE);
        usuario.setNombre("Test User");

        when(authService.authenticate("test@example.com", "password123"))
                .thenReturn(usuario);
        when(jwtService.generateToken(any(), any()))
                .thenReturn("mockJwtToken");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void login_WithInvalidCredentials_ReturnsUnauthorized() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword");

        when(authService.authenticate("test@example.com", "wrongpassword"))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}