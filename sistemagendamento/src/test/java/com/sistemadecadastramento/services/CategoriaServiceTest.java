package com.sistemadecadastramento.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sistemadecadastramento.dtos.CategoriaRequestDto;
import com.sistemadecadastramento.dtos.CategoriaResponseDto;
import com.sistemadecadastramento.exceptions.CategoriaJaCadastradaException;
import com.sistemadecadastramento.exceptions.CategoriaNaoEncontradaException;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {
    @InjectMocks
    private CategoriaService service;

    @Mock
    private CategoriaRepository repository;

    private CategoriaRequestDto requestDto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        requestDto = new CategoriaRequestDto();
        requestDto.setNome("Eletrônicos");
        requestDto.setDescricao("Produtos eletrônicos em geral");

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");
        categoria.setDescricao("Produtos eletrônicos em geral");
        categoria.setAtivo(true);
    }

    @Test
    void salvarCriar_DeveSalvarERetornarDto_QuandoCategoriaNaoExiste() {
        
        when(repository.existsByNome(requestDto.getNome())).thenReturn(false);
        when(repository.save(any(Categoria.class))).thenAnswer(i -> {
            Categoria cat = i.getArgument(0);
            cat.setId(1L);
            return cat;
        });

        CategoriaResponseDto response = service.salvarCriar(requestDto);

        assertNotNull(response);
        assertEquals("Eletrônicos", response.getNome()); // Presumindo que o DTO tenha getNome()
        verify(repository, times(1)).save(any(Categoria.class));
    }

    @Test
    void salvarCriar_DeveLancarExcecao_QuandoNomeJaExiste() {

        when(repository.existsByNome(requestDto.getNome())).thenReturn(true);

        assertThrows(CategoriaJaCadastradaException.class, () -> {
            service.salvarCriar(requestDto);
        });
        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    void buscarPorId_DeveRetornarDto_QuandoEncontrado() {
        
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDto response = service.buscarPorId(1L);

        assertNotNull(response);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_DeveLancarExcecao_QuandoNaoEncontrado() {
        
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNaoEncontradaException.class, () -> {
            service.buscarPorId(99L);
        });
    }

    @Test
    void alterarAtividade_DeveAlternarStatus() {
        
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        assertTrue(categoria.isAtivo()); 

        service.alterarAtividade(1L);

        assertFalse(categoria.isAtivo());
        verify(repository, times(1)).save(categoria);
    }
}
