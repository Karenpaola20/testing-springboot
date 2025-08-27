package com.backend.estudiantes.repositories;

import com.backend.estudiantes.models.Role;
import com.backend.estudiantes.models.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UsuarioRepositoryTest {


    @Autowired
    private UsuarioRepository repository;

    @Test
    void findByEmail_UsuarioExistente_RetornaUsuario() {

        Usuario testUser = new Usuario(
                "Test",
                "User",
                "test@unicolombo.edu.co",
                "123",
                Role.ESTUDIANTE
        );


        Usuario guardado = repository.save(testUser);

        Optional<Usuario> encontradoOptional = repository.findByEmail(guardado.getEmail());

        assertTrue(encontradoOptional.isPresent(), "EL Usuario deberia existrir en la base de datos.");

        Usuario encontrado = encontradoOptional.get();

        assertEquals("test@unicolombo.edu.co", encontrado.getEmail(),
                "El email del usuario encontrado deberia coincidir con el de prueba");


    }



}