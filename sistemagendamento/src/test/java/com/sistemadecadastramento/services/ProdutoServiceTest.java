package com.sistemadecadastramento.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sistemadecadastramento.dtos.ProdutoRequestDto;
import com.sistemadecadastramento.dtos.ProdutoResponseDto;
import com.sistemadecadastramento.exceptions.CategoriaNaoEncontradaException;
import com.sistemadecadastramento.exceptions.ProdutoPrejuizoException;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {
    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository repository;

    @Mock
    private CategoriaService categoriaService;

    private ProdutoRequestDto requestDto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setAtivo(true);

        requestDto = new ProdutoRequestDto();
        requestDto.setCategoriaId(1L);
        requestDto.setCodigo("PROD-01");
        requestDto.setPrecoCusto(new BigDecimal("50.00"));
        requestDto.setPrecoVenda(new BigDecimal("100.00")); // Lucro de 50.00 (50%)
    }

    @Test
    void salvarCriar_DeveSalvarProdutoComSucesso_QuandoDadosValidos() {
        
        when(categoriaService.buscarIdCategoria(1L)).thenReturn(categoria);
        when(repository.existsByCodigo("PROD-01")).thenReturn(false);
        
        Produto produtoSalvo = new Produto();
        produtoSalvo.setId(1L);
        when(repository.save(any(Produto.class))).thenReturn(produtoSalvo);

        ProdutoResponseDto response = produtoService.salvarCriar(requestDto);

        assertNotNull(response);
        verify(repository, times(1)).save(any(Produto.class));
    }

    @Test
    void salvarCriar_DeveLancarExcecaoQuandoCategoriaInativa() {
        
        categoria.setAtivo(false);
        when(categoriaService.buscarIdCategoria(1L)).thenReturn(categoria);

        
        assertThrows(CategoriaNaoEncontradaException.class, () -> {
            produtoService.salvarCriar(requestDto);
        });
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void salvarCriar_DeveLancarExcecao_QuandoPrecoVendaMenorOuIgualCusto() {
        
        when(categoriaService.buscarIdCategoria(1L)).thenReturn(categoria);
        when(repository.existsByCodigo("PROD-01")).thenReturn(false);
        
        requestDto.setPrecoVenda(new BigDecimal("40.00")); // Preço de venda menor que custo

        
        assertThrows(ProdutoPrejuizoException.class, () -> {
            produtoService.salvarCriar(requestDto);
        });
    }

    @Test
    void alterarAtividade_DeveAlternarStatus() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setAtivo(true);
        produto.setCategoria(categoria);

        when(repository.findById(1L)).thenReturn(Optional.of(produto));

       
        produtoService.alterarAtividade(1L);

        
        assertFalse(produto.getAtivo());
        verify(repository, times(1)).save(produto);
    }
}
