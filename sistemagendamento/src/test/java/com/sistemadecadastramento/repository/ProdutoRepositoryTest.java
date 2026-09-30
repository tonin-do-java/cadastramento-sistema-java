package com.sistemadecadastramento.repository;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de INTEGRAÇÃO do ProdutoRepository, usando @DataJpaTest
 * (banco H2 em memória, rollback automático ao final de cada teste).
 *
 * OBS: Roles.ADMIN é usado apenas como valor válido do enum para satisfazer a
 * coluna "role" do Usuario; ajuste para outro valor do seu enum se preferir.
 *
 * Requer no pom.xml/build.gradle: spring-boot-starter-test + com.h2database:h2 (escopo test).
 */
@DataJpaTest
@ActiveProfiles("test")
@Tag("integration")
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private Categoria criarCategoria(String nome, boolean ativo) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setDescricao("Descrição de " + nome);
        categoria.setAtivo(ativo);
        entityManager.persist(categoria);
        return categoria;
    }

    private Usuario criarUsuario(String nome, String email) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash("$2a$10$hashDeSenhaFakeParaTeste"); // valor fake, não é validado neste teste
        usuario.setRole(Roles.ADMIN);
        entityManager.persist(usuario);
        return usuario;
    }

    private Produto criarProduto(String codigo, String nome, Categoria categoria, Usuario usuario) {
        Produto produto = new Produto();
        produto.setCodigo(codigo);
        produto.setNome(nome);
        produto.setCategoria(categoria);
        produto.setUsuario(usuario);
        produto.setDescricao("Descrição de " + nome);
        produto.setMarca("Marca Teste");
        produto.setUnidadeMedida("UN");
        produto.setPrecoCusto(new BigDecimal("10.00"));
        produto.setPrecoVenda(new BigDecimal("20.00"));
        produto.setMargemLucro(new BigDecimal("50.00"));
        produto.setEstoqueMinimo(1);
        produto.setEstoqueMaximo(100);
        produto.setQuantidadeAtual(10);
        produto.setAtivo(true);
        return repository.save(produto);
    }

    @Test
    @DisplayName("findByCodigo deve retornar produto(s) com o código exato")
    void deveEncontrarPorCodigo() {
        Categoria categoria = criarCategoria("Eletrônicos", true);
        Usuario usuario = criarUsuario("Fulano", "fulano@teste.com");
        criarProduto("COD-123", "Mouse", categoria, usuario);

        List<Produto> encontrados = repository.findByCodigo("COD-123");

        assertEquals(1, encontrados.size());
        assertEquals("Mouse", encontrados.get(0).getNome());
    }

    @Test
    @DisplayName("findByNomeContainingIgnoreCase deve ignorar caixa e casar parcialmente")
    void deveEncontrarPorNomeIgnorandoCaixa() {
        Categoria categoria = criarCategoria("Papelaria", true);
        Usuario usuario = criarUsuario("Ciclana", "ciclana@teste.com");
        criarProduto("COD-001", "Caneta Azul", categoria, usuario);
        criarProduto("COD-002", "Caneta Vermelha", categoria, usuario);
        criarProduto("COD-003", "Lápis", categoria, usuario);

        List<Produto> encontrados = repository.findByNomeContainingIgnoreCase("caneta");

        assertEquals(2, encontrados.size());
    }

    @Test
    @DisplayName("findByCategoriaId deve retornar apenas produtos da categoria informada")
    void deveEncontrarPorCategoriaId() {
        Categoria categoriaA = criarCategoria("Categoria A", true);
        Categoria categoriaB = criarCategoria("Categoria B", true);
        Usuario usuario = criarUsuario("Beltrano", "beltrano@teste.com");

        criarProduto("COD-A1", "Produto A1", categoriaA, usuario);
        criarProduto("COD-B1", "Produto B1", categoriaB, usuario);

        List<Produto> encontrados = repository.findByCategoriaId(categoriaA.getId());

        assertEquals(1, encontrados.size());
        assertEquals("Produto A1", encontrados.get(0).getNome());
    }

    @Test
    @DisplayName("existsByCodigo deve retornar true quando o código já existe")
    void deveRetornarTrueQuandoCodigoExiste() {
        Categoria categoria = criarCategoria("Categoria X", true);
        Usuario usuario = criarUsuario("Sicrano", "sicrano@teste.com");
        criarProduto("COD-EXISTE", "Produto Existente", categoria, usuario);

        assertTrue(repository.existsByCodigo("COD-EXISTE"));
        assertFalse(repository.existsByCodigo("COD-NAO-EXISTE"));
    }

    @Test
    @DisplayName("save deve persistir produto com valores padrão corretos")
    void devePersistirProdutoComSucesso() {
        Categoria categoria = criarCategoria("Categoria Padrão", true);
        Usuario usuario = criarUsuario("Usuário Padrão", "padrao@teste.com");

        Produto salvo = criarProduto("COD-NOVO", "Produto Novo", categoria, usuario);

        assertTrue(salvo.getId() > 0);
        Produto recarregado = entityManager.find(Produto.class, salvo.getId());
        assertEquals("Produto Novo", recarregado.getNome());
        assertTrue(recarregado.getAtivo());
    }
}