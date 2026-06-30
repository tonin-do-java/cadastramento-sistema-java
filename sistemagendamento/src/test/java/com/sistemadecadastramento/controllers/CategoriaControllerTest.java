package com.sistemadecadastramento.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.services.CategoriaService;

@ExtendWith(MockitoExtension.class)
public class CategoriaControllerTest {
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

    @Test
    void listarTodos_DeveRetornarOkComLista() {
        
        when(service.listarTodos()).thenReturn(List.of(responseDto));

        ResponseEntity<List<CategoriaResponseDto>> response = controller.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service, times(1)).listarTodos();
    }

    @Test
    void buscarPorId_DeveRetornarOk_QuandoExistir() {
        
        when(service.buscarPorId(1L)).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).buscarPorId(1L);
    }

    @Test
    void criarCategoria_DeveRetornarCreatedComUri() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        
        when(service.salvarCriar(any(CategoriaRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.criarCategoria(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation()); 
        verify(service, times(1)).salvarCriar(requestDto);
    }

    @Test
    void atualizarCategoria_DeveRetornarOk() {
        
        CategoriaRequestDto requestDto = new CategoriaRequestDto();
        when(service.salvarAtualizar(eq(1L), any(CategoriaRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.atualizarCategoria(1L, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).salvarAtualizar(1L, requestDto);
    }

    @Test
    void alterarAtividade_DeveRetornarOk() {

        when(service.alterarAtividade(1L)).thenReturn(responseDto);

        ResponseEntity<CategoriaResponseDto> response = controller.alterarAtividade(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, times(1)).alterarAtividade(1L);
    }
}
