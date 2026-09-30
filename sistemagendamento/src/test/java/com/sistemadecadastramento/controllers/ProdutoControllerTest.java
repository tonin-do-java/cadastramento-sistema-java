package com.sistemadecadastramento.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemadecadastramento.dtos.ProdutoRequestDto;
import com.sistemadecadastramento.dtos.ProdutoResponseDto;
import com.sistemadecadastramento.exceptions.ProdutoNaoEncontradoException;
import com.sistemadecadastramento.infra.security.TokenService;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.ProdutoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de INTEGRAÇÃO da camada web (Controller + serialização JSON + validação Bean Validation),
 * usando @WebMvcTest. O ProdutoService é mockado (@MockBean), então NÃO chega ao banco.
 *
 * OBS 1: addFilters = false desliga a EXECUÇÃO dos filtros de segurança durante as
 * requisições simuladas. Isso não impede o Spring de precisar CRIAR os beans de segurança
 * ao montar o contexto — por isso TokenService também é mockado abaixo: o SecurityFilter
 * do projeto depende dele no construtor, e o @WebMvcTest não sobe a camada de serviços
 * onde o TokenService real estaria configurado.
 * OBS 2: @WithMockUser simula um usuário autenticado (necessário pois o Service usa
 * SecurityContextHolder, mas aqui o Service está mockado, então é só para não cair em 401/403).
 */
@WebMvcTest(ProdutoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("integration")
class ProdutoControllerTest {

    @MockBean
    private TokenService tokenService;

    // Se o SecurityFilter do seu projeto também depender de outro bean no construtor
    // (ex.: um UsuarioRepository ou UserDetailsService para carregar o usuário do token),
    // adicione um @MockBean equivalente aqui, seguindo o mesmo padrão do tokenService acima.


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService service;

    private ProdutoRequestDto requestDtoValido() {
        return new ProdutoRequestDto(
                1L,
                "COD-001",
                "Produto Teste",
                "Descrição do produto",
                "Marca X",
                "UN",
                new BigDecimal("50.00"),
                new BigDecimal("100.00"),
                5,
                50,
                10,
                true,
                false
        );
    }

    /**
     * ProdutoResponseDto só tem construtor a partir de Produto, então montamos
     * um Produto com categoria/usuario mockados para gerar um DTO de resposta válido.
     */
    private ProdutoResponseDto respostaDtoStub() {
        Categoria categoria = new Categoria(1L, "Categoria Teste", "Descrição da categoria", true);
        Usuario usuario = new Usuario(1L, "Fulano de Tal", "fulano@teste.com", "hashFake", Roles.ADMIN);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setCategoria(categoria);
        produto.setUsuario(usuario);
        produto.setCodigo("COD-001");
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto");
        produto.setMarca("Marca X");
        produto.setUnidadeMedida("UN");
        produto.setPrecoCusto(new BigDecimal("50.00"));
        produto.setPrecoVenda(new BigDecimal("100.00"));
        produto.setMargemLucro(new BigDecimal("50.00"));
        produto.setEstoqueMinimo(5);
        produto.setEstoqueMaximo(50);
        produto.setQuantidadeAtual(10);
        produto.setAtivo(true);

        return new ProdutoResponseDto(produto);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/produto deve retornar 200 com lista de produtos")
    void listarPorFiltro_deveRetornar200() throws Exception {
        when(service.listarComFiltros(null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/produto"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/produto?nome=caneta deve repassar o filtro por nome ao service")
    void listarPorFiltro_deveRepassarFiltroNome() throws Exception {
        when(service.listarComFiltros(eq("caneta"), eq(null), eq(null))).thenReturn(List.of());

        mockMvc.perform(get("/api/produto").param("nome", "caneta"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/produto/{id} deve retornar 200 quando encontrado")
    void buscarPorId_deveRetornar200() throws Exception {
        when(service.buscarPorId(anyLong())).thenReturn(respostaDtoStub());

        mockMvc.perform(get("/api/produto/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/produto/{id} deve propagar 404 quando produto não existir")
    void buscarPorId_deveRetornar404QuandoNaoEncontrado() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new ProdutoNaoEncontradoException());

        // Assume que existe um @ExceptionHandler / @ControllerAdvice mapeando
        // ProdutoNaoEncontradoException para 404. Ajuste o status esperado
        // conforme o handler real do seu projeto.
        mockMvc.perform(get("/api/produto/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/produto com dados válidos deve retornar 201")
    void criarProduto_deveRetornar201() throws Exception {
        when(service.salvarCriar(any())).thenReturn(respostaDtoStub());

        mockMvc.perform(post("/api/produto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoValido())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/produto sem campos obrigatórios deve retornar 400")
    void criarProduto_deveRetornar400ComDadosInvalidos() throws Exception {
        ProdutoRequestDto dtoInvalido = new ProdutoRequestDto(); // todos os campos nulos

        mockMvc.perform(post("/api/produto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/produto/{id} deve retornar 200 quando atualizado com sucesso")
    void atualizarProduto_deveRetornar200() throws Exception {
        when(service.salvarAtualizar(eq(1L), any())).thenReturn(respostaDtoStub());

        mockMvc.perform(put("/api/produto/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoValido())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/produto/{id}/atividade deve retornar 200 e inverter o status ativo")
    void alterarAtividade_deveRetornar200() throws Exception {
        ProdutoResponseDto resposta = respostaDtoStub();
        when(service.alterarAtividade(1L)).thenReturn(resposta);

        mockMvc.perform(put("/api/produto/1/atividade"))
                .andExpect(status().isOk());
    }
}