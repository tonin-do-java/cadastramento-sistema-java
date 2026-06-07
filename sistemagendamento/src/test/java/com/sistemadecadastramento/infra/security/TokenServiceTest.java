package com.sistemadecadastramento.infra.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.sistemadecadastramento.models.Usuario;

public class TokenServiceTest {
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        
        ReflectionTestUtils.setField(tokenService, "chaveApi", "uma-secret-de-teste-muito-segura-com-mais-de-32-caracteres");
    }

    @Test
    @DisplayName("Teste 4: Deve gerar um token JWT válido contendo o e-mail do usuário no subject")
    void deveGerarTokenJwtValido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("desenvolvedor@email.com");

        
        String token = tokenService.gerarToken(usuario);

        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Teste 5: Deve decodificar um token válido e retornar o e-mail correto")
    void deveValidarTokenEExtrairOEmailDoUsuario() {
        
        Usuario usuario = new Usuario();
        usuario.setEmail("validador@email.com");
        
        
        String tokenGerado = tokenService.gerarToken(usuario);

        
        String emailExtraido = tokenService.validarToken(tokenGerado);

        
        assertEquals("validador@email.com", emailExtraido);
    }
}
