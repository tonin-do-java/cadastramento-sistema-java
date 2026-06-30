package com.sistemadecadastramento.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

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

import com.sistemadecadastramento.dtos.MovimentacaoRequestDto;
import com.sistemadecadastramento.dtos.MovimentacaoResponseDto;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.models.MovimentacaoEstoque;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.MovimentacaoService;

@ExtendWith(MockitoExtension.class)
public class MovimentacaoServiceTest {
    @InjectMocks
    private MovimentacoesController controller;

    @Mock
    private MovimentacaoService service;

    @Test
    void listarTodos_DeveRetornarOkComLista() {
        
        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste"); 
        produto.setCategoria(categoria);  

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setId(1L);
        movimentacao.setProduto(produto);
        when(service.listarHistorico()).thenReturn(Collections.emptyList());

        ResponseEntity<List<MovimentacaoResponseDto>> response = controller.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).listarHistorico();
    }

    @Test
    void registrarHistorico_DeveRetornarCreated() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        MovimentacaoRequestDto requestDto = new MovimentacaoRequestDto();

        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste"); 
        produto.setCategoria(categoria); 

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Admin Teste"); 

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setId(1L);
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);

        MovimentacaoResponseDto responseDto = new MovimentacaoResponseDto(movimentacao);
        
        when(service.registrarMovimentacao(any(MovimentacaoRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<MovimentacaoResponseDto> responseEntity = controller.registrarHistorico(requestDto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        verify(service, times(1)).registrarMovimentacao(requestDto);
    }
}
