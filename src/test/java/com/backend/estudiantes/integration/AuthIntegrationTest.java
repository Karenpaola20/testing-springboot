package com.backend.estudiantes.integration;

import com.backend.estudiantes.dto.LoginRequest;
import com.backend.estudiantes.models.Role;
import com.backend.estudiantes.models.Usuario;
import com.backend.estudiantes.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void login_WithValidCredentials_ReturnsJwtToken() {
        String email = "test@example.com";
        String password = "password123";

        Usuario usuario = new Usuario();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Role.ESTUDIANTE);
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest(email, password);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                Map.class
        );

        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println("Response Headers: " + response.getHeaders());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody();
        responseBody.forEach((key, value) -> System.out.println(key + " = " + value));

        assertTrue(responseBody.containsKey("token"), "Response should contain 'token' key");
        assertTrue(responseBody.containsKey("usuario"), "Response should contain 'usuario' object");

        @SuppressWarnings("unchecked")
        Map<String, Object> usuarioResponse = (Map<String, Object>) responseBody.get("usuario");

        assertTrue(usuarioResponse.containsKey("name") || usuarioResponse.containsKey("nombre"),
                "Usuario object should contain 'name' or 'nombre' key");
        assertTrue(usuarioResponse.containsKey("apellido"), "Usuario object should contain 'apellido' key");
        assertTrue(usuarioResponse.containsKey("email"), "Usuario object should contain 'email' key");
        assertTrue(usuarioResponse.containsKey("rol"), "Usuario object should contain 'rol' key");
        assertTrue(usuarioResponse.containsKey("id"), "Usuario object should contain 'id' key");

        if (usuarioResponse.containsKey("name")) {
            assertEquals("Test", usuarioResponse.get("name"));
        }
        assertEquals("User", usuarioResponse.get("apellido"));
        assertEquals("test@example.com", usuarioResponse.get("email"));
        assertEquals("ESTUDIANTE", usuarioResponse.get("rol"));

        assertNotNull(responseBody.get("token"));
        assertFalse(((String) responseBody.get("token")).isEmpty());
    }

    @Test
    void login_WithInvalidCredentials_ReturnsUnauthorized() {
        String email = "test@example.com";
        String password = "password123";

        Usuario usuario = new Usuario();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Role.ESTUDIANTE);
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest(email, "wrongpassword");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                Map.class
        );

        System.out.println("Invalid Credentials Response: " + response.getBody());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message") || response.getBody().containsKey("error"));
    }
}