package com.sistemadecadastramento.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Categoria (model) - testes unitários")
class CategoriaTest {

    @Test
    @DisplayName("Uma categoria recém-instanciada (sem setAtivo) deve começar inativa (false)")
    void ativo_DeveComecarComoFalse_PorPadraoDoJava() {
        Categoria categoria = new Categoria();

        assertFalse(categoria.isAtivo(),
                "boolean primitivo tem default false; quem garante 'nasce ativa' é o CategoriaService.salvarCriar");
    }

    @Test
    @DisplayName("Construtor completo deve preencher todos os campos")
    void construtorCompleto_DevePreencherCampos() {
        Categoria categoria = new Categoria(1L, "Eletrônicos", "Descrição", true);

        assertEquals(1L, categoria.getId());
        assertEquals("Eletrônicos", categoria.getNome());
        assertEquals("Descrição", categoria.getDescricao());
        assertEquals(true, categoria.isAtivo());
    }

    @Test
    @DisplayName("equals/hashCode (Lombok @Data) devem considerar todos os campos")
    void equalsEHashCode() {
        Categoria a = new Categoria(1L, "Eletrônicos", "Descrição", true);
        Categoria b = new Categoria(1L, "Eletrônicos", "Descrição", true);
        Categoria c = new Categoria(2L, "Móveis", "Outra descrição", false);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("setAtivo deve alternar corretamente o estado")
    void setAtivo_DeveAlterarEstado() {
        Categoria categoria = new Categoria();
        categoria.setAtivo(true);
        assertEquals(true, categoria.isAtivo());

        categoria.setAtivo(false);
        assertFalse(categoria.isAtivo());
    }
}