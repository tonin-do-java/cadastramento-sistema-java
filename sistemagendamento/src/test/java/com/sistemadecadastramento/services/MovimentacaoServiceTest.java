package com.sistemadecadastramento.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sistemadecadastramento.dtos.MovimentacaoRequestDto;
import com.sistemadecadastramento.dtos.MovimentacaoResponseDto;
import com.sistemadecadastramento.exceptions.SaldoInsuficienteException;
import com.sistemadecadastramento.models.MovimentacaoEstoque;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.models.TipoMovimentacao;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.MovimentacaoRepository;
import com.sistemadecadastramento.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class MovimentacaoServiceTest {
    @InjectMocks
    private MovimentacaoService movimentacaoService;

    @Mock
    private MovimentacaoRepository repository;
    
    @Mock
    private ProdutoRepository produtoRepository;
    
    @Mock
    private ProdutoService produtoService;
    
    @Mock
    private UsuarioService usuarioService;
    
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private Produto produto;
    private Usuario usuario;
    private MovimentacaoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Configurando Mock do Security Context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("admin@teste.com");

        produto = new Produto();
        produto.setId(1L);
        produto.setQuantidadeAtual(10);
        produto.setControlaLote(false);
        produto.setControlaValidade(false);

        usuario = new Usuario();
        usuario.setEmail("admin@teste.com");

        requestDto = new MovimentacaoRequestDto();
        requestDto.setProdutoId(1L);
        requestDto.setQuantidade(5);
    }

    @Test
    void registrarMovimentacao_Entrada_DeveAumentarEstoque() {
        // Arrange
        requestDto.setTipo(TipoMovimentacao.ENTRADA);
        
        when(produtoService.buscarId(1L)).thenReturn(produto);
        when(usuarioService.buscarPorEmail("admin@teste.com")).thenReturn(usuario);
        when(repository.save(any(MovimentacaoEstoque.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        MovimentacaoResponseDto response = movimentacaoService.registrarMovimentacao(requestDto);

        // Assert
        assertEquals(15, produto.getQuantidadeAtual()); // 10 + 5
        verify(produtoRepository, times(1)).save(produto);
        verify(repository, times(1)).save(any(MovimentacaoEstoque.class));
        assertNotNull(response);
    }

    @Test
    void registrarMovimentacao_Saida_DeveDiminuirEstoque() {
        // Arrange
        requestDto.setTipo(TipoMovimentacao.SAIDA);
        
        when(produtoService.buscarId(1L)).thenReturn(produto);
        when(usuarioService.buscarPorEmail("admin@teste.com")).thenReturn(usuario);
        when(repository.save(any(MovimentacaoEstoque.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        movimentacaoService.registrarMovimentacao(requestDto);

        // Assert
        assertEquals(5, produto.getQuantidadeAtual()); // 10 - 5
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void registrarMovimentacao_Saida_DeveLancarExcecao_QuandoSaldoInsuficiente() {
        // Arrange
        requestDto.setTipo(TipoMovimentacao.SAIDA);
        requestDto.setQuantidade(15); // Maior que o saldo de 10
        
        when(produtoService.buscarId(1L)).thenReturn(produto);
        when(usuarioService.buscarPorEmail("admin@teste.com")).thenReturn(usuario);

        // Act & Assert
        assertThrows(SaldoInsuficienteException.class, () -> {
            movimentacaoService.registrarMovimentacao(requestDto);
        });
        verify(produtoRepository, never()).save(produto); // Garante que não salvou o produto
    }
}
