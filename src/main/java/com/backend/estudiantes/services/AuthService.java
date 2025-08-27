package com.backend.estudiantes.services;

import com.backend.estudiantes.models.Role;
import com.backend.estudiantes.models.Usuario;
import com.backend.estudiantes.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_UsuarioExistenteYPasswordCorrecto_RetornaUsuario() {
        // Arrange
        String email = "test@email.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        Usuario usuario = new Usuario("Test", "User", email, encodedPassword, Role.ESTUDIANTE);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        Usuario result = authService.authenticate(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }

    @Test
    void authenticate_UsuarioNoExistente_LanzaRuntimeException() {
        String email = "nonexistent@email.com";
        String password = "password123";

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(email, password));

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticate_PasswordIncorrecto_LanzaRuntimeException() {
        String email = "test@email.com";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword123";

        Usuario usuario = new Usuario("Test", "User", email, encodedPassword, Role.ESTUDIANTE);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(email, password));

        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }
}