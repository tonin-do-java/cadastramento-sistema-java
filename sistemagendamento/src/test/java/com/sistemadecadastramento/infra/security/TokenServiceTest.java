package com.sistemadecadastramento.infra.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;

@DisplayName("TokenService - testes unitários")
class TokenServiceTest {

    private static final String CHAVE_VALIDA = "uma-secret-de-teste-muito-segura-com-mais-de-32-caracteres";

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "chaveApi", CHAVE_VALIDA);
    }

    private Usuario criarUsuario(String email, String nome) {
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setNome(nome);
        u.setRole(Roles.USER);
        return u;
    }

    // ---------- gerarToken ----------

    @Test
    @DisplayName("Deve gerar um token JWT válido com 3 partes (header.payload.signature)")
    void gerarToken_DeveGerarTokenValido() {
        Usuario usuario = criarUsuario("desenvolvedor@email.com", "Dev");

        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Tokens gerados em momentos diferentes para usuários diferentes devem ser distintos")
    void gerarToken_DeveSerDistintoEntreUsuarios() {
        String token1 = tokenService.gerarToken(criarUsuario("a@email.com", "A"));
        String token2 = tokenService.gerarToken(criarUsuario("b@email.com", "B"));

        assertNotEquals(token1, token2);
    }

    // ---------- validarToken (caminho feliz) ----------

    @Test
    @DisplayName("Deve validar um token gerado por este mesmo serviço e devolver o email do usuário")
    void validarToken_DeveExtrairEmail_QuandoTokenValido() {
        Usuario usuario = criarUsuario("validador@email.com", "Validador");
        String token = tokenService.gerarToken(usuario);

        String emailExtraido = tokenService.validarToken(token);

        assertEquals("validador@email.com", emailExtraido);
    }

    // ---------- validarToken (casos de erro) ----------

    @Test
    @DisplayName("Deve lançar exceção ao validar um token com assinatura de outra chave")
    void validarToken_DeveLancarExcecao_QuandoAssinadoComOutraChave() {
        TokenService outroServico = new TokenService();
        ReflectionTestUtils.setField(outroServico, "chaveApi",
                "outra-secret-totalmente-diferente-com-mais-de-32-caracteres");
        String tokenDeOutraChave = outroServico.gerarToken(criarUsuario("x@email.com", "X"));

        assertThrows(SignatureException.class, () -> tokenService.validarToken(tokenDeOutraChave));
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar um token malformado")
    void validarToken_DeveLancarExcecao_QuandoTokenMalformado() {
        assertThrows(JwtException.class, () -> tokenService.validarToken("isso.nao-e.um-jwt-valido"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar uma string vazia")
    void validarToken_DeveLancarExcecao_QuandoTokenVazio() {
        // jjwt valida o argumento antes de tentar fazer parsing: uma string vazia
        // nem chega a ser tratada como JWT malformado, e sim rejeitada de cara
        // com IllegalArgumentException (não é uma JwtException).
        assertThrows(IllegalArgumentException.class, () -> tokenService.validarToken(""));
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar um token com payload adulterado")
    void validarToken_DeveLancarExcecao_QuandoTokenAdulterado() {
        String tokenValido = tokenService.gerarToken(criarUsuario("real@email.com", "Real"));
        String[] partes = tokenValido.split("\\.");
        // corrompe um caractere do payload (parte do meio) sem recalcular a assinatura
        String payloadAdulterado = partes[1].equals("A") ? "B" : "A" + partes[1].substring(1);
        String tokenAdulterado = partes[0] + "." + payloadAdulterado + "." + partes[2];

        assertThrows(JwtException.class, () -> tokenService.validarToken(tokenAdulterado));
    }
}