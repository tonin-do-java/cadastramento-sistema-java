package com.sistemadecadastramento.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("DTOs de Usuario - validação e mapeamento")
class UsuarioDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void encerrar() {
        factory.close();
    }

    private Set<String> camposInvalidos(Object dto) {
        return validator.validate(dto).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    // ---------- UsuarioRequestDto ----------

    @Test
    @DisplayName("UsuarioRequestDto válido não deve gerar violações")
    void requestDto_Valido() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Natan", "natan@email.com", Roles.USER);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("UsuarioRequestDto: role é opcional")
    void requestDto_RoleOpcional() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Natan", "natan@email.com", null);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("UsuarioRequestDto: nome nulo ou em branco é inválido")
    void requestDto_NomeInvalido() {
        assertEquals(Set.of("nome"), camposInvalidos(new UsuarioRequestDto(null, "a@b.com", null)));
        assertEquals(Set.of("nome"), camposInvalidos(new UsuarioRequestDto("   ", "a@b.com", null)));
    }

    @Test
    @DisplayName("UsuarioRequestDto: e-mail em branco ou com formato inválido é rejeitado")
    void requestDto_EmailInvalido() {
        assertEquals(Set.of("email"), camposInvalidos(new UsuarioRequestDto("Natan", "", null)));
        assertEquals(Set.of("email"), camposInvalidos(new UsuarioRequestDto("Natan", "sem-arroba", null)));
    }

    // ---------- UsuarioCreateRequestDto ----------

    private UsuarioCreateRequestDto criarDtoValido() {
        UsuarioCreateRequestDto dto = new UsuarioCreateRequestDto();
        dto.setNome("Natan");
        dto.setEmail("natan@email.com");
        dto.setSenha("12345678");
        dto.setConfirmacaoSenha("12345678");
        return dto;
    }

    @Test
    @DisplayName("UsuarioCreateRequestDto válido não deve gerar violações")
    void createDto_Valido() {
        assertTrue(validator.validate(criarDtoValido()).isEmpty());
    }

    @Test
    @DisplayName("UsuarioCreateRequestDto: herda as validações de nome e e-mail")
    void createDto_HerdaValidacoes() {
        UsuarioCreateRequestDto dto = criarDtoValido();
        dto.setNome("");
        dto.setEmail("invalido");

        assertEquals(Set.of("nome", "email"), camposInvalidos(dto));
    }

    @Test
    @DisplayName("UsuarioCreateRequestDto: senha em branco é inválida")
    void createDto_SenhaEmBranco() {
        UsuarioCreateRequestDto dto = criarDtoValido();
        dto.setSenha("");

        assertTrue(camposInvalidos(dto).contains("senha"));
    }

    @Test
    @DisplayName("UsuarioCreateRequestDto: senha e confirmação com menos de 8 caracteres são inválidas")
    void createDto_SenhaCurta() {
        UsuarioCreateRequestDto dto = criarDtoValido();
        dto.setSenha("1234567");
        dto.setConfirmacaoSenha("1234567");

        assertEquals(Set.of("senha", "confirmacaoSenha"), camposInvalidos(dto));
    }

    @Test
    @DisplayName("UsuarioCreateRequestDto: senha com exatamente 8 caracteres é aceita")
    void createDto_SenhaNoLimite() {
        UsuarioCreateRequestDto dto = criarDtoValido();
        dto.setSenha("12345678");
        dto.setConfirmacaoSenha("12345678");

        assertTrue(validator.validate(dto).isEmpty());
    }

    // ---------- UsuarioResponseDto ----------

    @Test
    @DisplayName("UsuarioResponseDto deve copiar id, nome, e-mail e role da entidade")
    void responseDto_DeveMapearEntidade() {
        Usuario u = new Usuario(5L, "Natan", "natan@email.com", "hash-secreto", Roles.ADMIN);

        UsuarioResponseDto dto = new UsuarioResponseDto(u);

        assertEquals(5L, dto.getId());
        assertEquals("Natan", dto.getNome());
        assertEquals("natan@email.com", dto.getEmail());
        assertEquals(Roles.ADMIN, dto.getRole());
    }

    @Test
    @DisplayName("UsuarioResponseDto não deve expor senha/hash")
    void responseDto_NaoExpoeSenha() {
        boolean expoeSenha = java.util.Arrays.stream(UsuarioResponseDto.class.getDeclaredFields())
                .map(f -> f.getName().toLowerCase())
                .anyMatch(n -> n.contains("senha") || n.contains("password") || n.contains("hash"));

        assertEquals(false, expoeSenha);
    }

    @Test
    @DisplayName("UsuarioResponseDto aceita campos nulos da entidade")
    void responseDto_CamposNulos() {
        UsuarioResponseDto dto = new UsuarioResponseDto(new Usuario());

        assertNull(dto.getId());
        assertNull(dto.getRole());
    }
}