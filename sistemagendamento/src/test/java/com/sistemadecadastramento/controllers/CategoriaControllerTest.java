package com.sistemadecadastramento.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sistemadecadastramento.dtos.CategoriaRequestDto;
import com.sistemadecadastramento.dtos.CategoriaResponseDto;
import com.sistemadecadastramento.exceptions.CategoriaJaCadastradaException;
import com.sistemadecadastramento.exceptions.CategoriaNaoEncontradaException;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.services.CategoriaService;

/**
 * NOTA: não recebi o CategoriaController.java, então este teste segue o mesmo
 * padrão do que já existia no projeto (chamada direta aos métodos do controller,
 * sem MockMvc), assumindo as assinaturas já usadas: listarTodos(), buscarPorId(Long),
 * criarCategoria(CategoriaRequestDto), atualizarCategoria(Long, CategoriaRequestDto)
 * e alterarAtividade(Long). Se as assinaturas reais forem diferentes, ajustar.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaController - testes unitários")
class CategoriaControllerTest {

    @InjectMocks
    private CategoriaController controller;

    @Mock
    private CategoriaService service;

    private Categoria categoriaBase;
    private CategoriaResponseDto responseDto;

    @BeforeEach
    void setUp() {
        categoriaBase = new Categoria();
        categoriaBase.setId(1L);
        categoriaBase.setNome("Eletrônicos");
        categoriaBase.setDescricao("Descrição teste");
        categoriaBase.setAtivo(true);

        responseDto = new CategoriaResponseDto(categoriaBase);
    }

    @AfterEach
    void limparContextoRequisicao() {
        RequestContextHolder.resetRequestAttributes();
    }

    // ---------- listarTodos ----------

    @Test
    @DisplayName("listarTodos deve retornar 200 com a lista de categorias")
    void listarTodos_DeveRetornarOkComLista() {
        when(service.listarTodos()).thenReturn(List.of(responseDto));

        ResponseEntity<List<CategoriaResponseDto>> response = controller.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service, times(1)).listarTodos();
    }

    @Test
    @DisplayName("listarTodos deve retornar 200 com lista vazia quando não há categorias")
    void listarTodos_DeveRetornarListaVazia() {
        when(service.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<CategoriaResponseDto>> response = controller.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ---------- buscarPorId ----------

    @Test
    @DisplayName("buscarPorId deve retornar 200 quando a categoria existir")
    void buscarPorId_DeveRetornarOk_QuandoExistir() {
        when(service.buscarPorId(1L)).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Eletrônicos", response.getBody().getNome());
        verify(service, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("buscarPorId deve propagar a exceção quando o service não encontrar a categoria")
    void buscarPorId_DevePropagarExcecao_QuandoNaoExistir() {
        when(service.buscarPorId(99L)).thenThrow(new CategoriaNaoEncontradaException());

        assertThrows(CategoriaNaoEncontradaException.class, () -> controller.buscarPorId(99L));
    }

    // ---------- criarCategoria ----------

    @Test
    @DisplayName("criarCategoria deve retornar 201, Location com o ID criado e o corpo da categoria")
    void criarCategoria_DeveRetornarCreatedComUri() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        requestDto.setNome("Eletrônicos");
        requestDto.setDescricao("Descrição teste");

        when(service.salvarCriar(any(CategoriaRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.criarCategoria(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().endsWith("/1"),
                "A URI deveria terminar com o ID da categoria criada");
        assertEquals("Eletrônicos", response.getBody().getNome());
        verify(service, times(1)).salvarCriar(requestDto);
    }

    @Test
    @DisplayName("criarCategoria deve propagar exceção quando o nome já existir, sem gerar Location")
    void criarCategoria_DevePropagarExcecao_QuandoNomeJaExistir() {
        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        requestDto.setNome("Duplicada");
        requestDto.setDescricao("Descrição");

        when(service.salvarCriar(any(CategoriaRequestDto.class)))
                .thenThrow(new CategoriaJaCadastradaException());

        assertThrows(CategoriaJaCadastradaException.class, () -> controller.criarCategoria(requestDto));
    }

    // ---------- atualizarCategoria ----------

    @Test
    @DisplayName("atualizarCategoria deve retornar 200 com a categoria atualizada")
    void atualizarCategoria_DeveRetornarOk() {
        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        requestDto.setNome("Nome Atualizado");
        requestDto.setDescricao("Descrição atualizada");

        when(service.salvarAtualizar(eq(1L), any(CategoriaRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.atualizarCategoria(1L, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).salvarAtualizar(1L, requestDto);
    }

    @Test
    @DisplayName("atualizarCategoria deve propagar exceção quando o ID não existir")
    void atualizarCategoria_DevePropagarExcecao_QuandoIdNaoExistir() {
        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        requestDto.setNome("Nome");
        requestDto.setDescricao("Descrição");

        when(service.salvarAtualizar(eq(99L), any(CategoriaRequestDto.class)))
                .thenThrow(new CategoriaNaoEncontradaException());

        assertThrows(CategoriaNaoEncontradaException.class,
                () -> controller.atualizarCategoria(99L, requestDto));

        verify(service, never()).salvarCriar(any());
    }

    // ---------- alterarAtividade ----------

    @Test
    @DisplayName("alterarAtividade deve retornar 200 com o novo status")
    void alterarAtividade_DeveRetornarOk() {
        when(service.alterarAtividade(1L)).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.alterarAtividade(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).alterarAtividade(1L);
    }

    @Test
    @DisplayName("alterarAtividade deve propagar exceção quando o ID não existir")
    void alterarAtividade_DevePropagarExcecao_QuandoIdNaoExistir() {
        when(service.alterarAtividade(99L)).thenThrow(new CategoriaNaoEncontradaException());

        assertThrows(CategoriaNaoEncontradaException.class, () -> controller.alterarAtividade(99L));
    }
}