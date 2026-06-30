package com.sistemadecadastramento.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.sistemadecadastramento.dtos.ProdutoRequestDto;
import com.sistemadecadastramento.dtos.ProdutoResponseDto;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.services.ProdutoService;

@ExtendWith(MockitoExtension.class)
public class ProdutoControllerTest {
    @InjectMocks
    private ProdutoController controller;

    @Mock
    private ProdutoService service;

    @Test
    void criarProduto_DeveRetornarCreated() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produtoMock = new Produto();
        produtoMock.setId(1L);
        produtoMock.setCategoria(categoria);

        ProdutoRequestDto requestDto = new ProdutoRequestDto();
        ProdutoResponseDto responseDto = new ProdutoResponseDto(produtoMock);
        
        when(service.salvarCriar(any(ProdutoRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<ProdutoResponseDto> responseEntity = controller.criarProduto(requestDto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        verify(service, times(1)).salvarCriar(any(ProdutoRequestDto.class));
    }

    @Test
    void buscarPorId_DeveRetornarOk() {
        
        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produtoMock = new Produto();
        produtoMock.setId(1L);
        produtoMock.setCategoria(categoria);

        when(service.buscarPorId(1L)).thenReturn(new ProdutoResponseDto(produtoMock));

        ResponseEntity<ProdutoResponseDto> responseEntity = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
