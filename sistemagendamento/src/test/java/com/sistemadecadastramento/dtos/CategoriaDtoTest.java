package com.sistemadecadastramento.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sistemadecadastramento.models.Categoria;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("DTOs de Categoria - validação e mapeamento")
class CategoriaDtoTest {

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

    private Set<String> camposInvalidos(CategoriaRequestDto dto) {
        return validator.validate(dto).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    // ---------- CategoriaRequestDto ----------

    @Test
    @DisplayName("CategoriaRequestDto válido não deve gerar violações")
    void requestDto_Valido() {
        CategoriaRequestDto dto = new CategoriaRequestDto("Eletrônicos", "Produtos eletrônicos");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("CategoriaRequestDto: nome nulo ou em branco é inválido")
    void requestDto_NomeInvalido() {
        assertEquals(Set.of("nome"), camposInvalidos(new CategoriaRequestDto(null, "Descrição válida")));
        assertEquals(Set.of("nome"), camposInvalidos(new CategoriaRequestDto("   ", "Descrição válida")));
        assertEquals(Set.of("nome"), camposInvalidos(new CategoriaRequestDto("", "Descrição válida")));
    }

    @Test
    @DisplayName("CategoriaRequestDto: descrição nula ou em branco é inválida")
    void requestDto_DescricaoInvalida() {
        assertEquals(Set.of("descricao"), camposInvalidos(new CategoriaRequestDto("Nome válido", null)));
        assertEquals(Set.of("descricao"), camposInvalidos(new CategoriaRequestDto("Nome válido", "   ")));
    }

    @Test
    @DisplayName("CategoriaRequestDto: nome e descrição em branco geram as duas violações")
    void requestDto_AmbosInvalidos() {
        assertEquals(Set.of("nome", "descricao"), camposInvalidos(new CategoriaRequestDto("", "")));
    }

    // ---------- CategoriaResponseDto ----------

    @Test
    @DisplayName("CategoriaResponseDto deve copiar id, nome, descrição e status da entidade")
    void responseDto_DeveMapearEntidade() {
        Categoria categoria = new Categoria(3L, "Móveis", "Móveis em geral", true);

        CategoriaResponseDto dto = new CategoriaResponseDto(categoria);

        assertEquals(3L, dto.getId());
        assertEquals("Móveis", dto.getNome());
        assertEquals("Móveis em geral", dto.getDescricao());
        assertTrue(dto.isAtivo());
    }

    @Test
    @DisplayName("CategoriaResponseDto deve refletir categoria inativa")
    void responseDto_DeveMapearCategoriaInativa() {
        Categoria categoria = new Categoria(4L, "Descontinuados", "Linha descontinuada", false);

        CategoriaResponseDto dto = new CategoriaResponseDto(categoria);

        assertFalse(dto.isAtivo());
    }
}