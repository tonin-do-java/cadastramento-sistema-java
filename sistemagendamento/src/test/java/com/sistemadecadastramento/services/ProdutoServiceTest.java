package com.sistemadecadastramento.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sistemadecadastramento.dtos.ProdutoRequestDto;
import com.sistemadecadastramento.dtos.ProdutoResponseDto;
import com.sistemadecadastramento.exceptions.CategoriaNaoEncontradaException;
import com.sistemadecadastramento.exceptions.ProdutoJaCadastradoException;
import com.sistemadecadastramento.exceptions.ProdutoNaoEncontradoException;
import com.sistemadecadastramento.exceptions.ProdutoPrejuizoException;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.ProdutoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes UNITÁRIOS de ProdutoService.
 * Todas as dependências (repository, categoriaService, usuarioService) são mockadas,
 * e o SecurityContextHolder é mockado estaticamente para simular o usuário logado.
 *
 * OBS: Categoria e Usuario são mockados com Mockito.mock(...) para não depender
 * da estrutura interna real dessas classes (construtores/setters).
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private UsuarioService usuarioService;

    private ProdutoService produtoService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    private static final String EMAIL_LOGADO = "usuario@teste.com";

    @BeforeEach
    void setUp() {
        // ProdutoService recebe (ProdutoRepository, CategoriaService, UsuarioService) via @RequiredArgsConstructor
        produtoService = new ProdutoService(repository, categoriaService, usuarioService);

        // lenient(): só salvarCriar/salvarAtualizar usam o SecurityContextHolder; os demais métodos
        // (listarComFiltros, buscarPorId, alterarAtividade) não o chamam, e o Mockito (com strict
        // stubbing) reclamaria de "unnecessary stubbing" se isso fosse um when(...) comum.
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(EMAIL_LOGADO);

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    private Categoria categoriaAtiva() {
        return new Categoria(1L, "Categoria Teste", "Descrição da categoria", true);
    }

    private Categoria categoriaInativa() {
        return new Categoria(1L, "Categoria Teste", "Descrição da categoria", false);
    }

    private Usuario usuarioLogado() {
        return new Usuario(1L, "Fulano de Tal", EMAIL_LOGADO, "hashFake", Roles.ADMIN);
    }

    private ProdutoRequestDto requestDtoValido() {
        return new ProdutoRequestDto(
                1L,
                "COD-001",
                "Produto Teste",
                "Descrição do produto teste",
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

    private Produto produtoValido() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setCategoria(categoriaAtiva());
        produto.setUsuario(usuarioLogado());
        produto.setCodigo("COD-001");
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto teste");
        produto.setMarca("Marca X");
        produto.setUnidadeMedida("UN");
        produto.setPrecoCusto(new BigDecimal("50.00"));
        produto.setPrecoVenda(new BigDecimal("100.00"));
        produto.setMargemLucro(new BigDecimal("50.00"));
        produto.setEstoqueMinimo(5);
        produto.setEstoqueMaximo(50);
        produto.setQuantidadeAtual(10);
        produto.setAtivo(true);
        return produto;
    }

    @Nested
    @DisplayName("listarComFiltros")
    class ListarComFiltros {

        @Test
        @DisplayName("deve filtrar por código quando informado")
        void deveFiltrarPorCodigo() {
            when(repository.findByCodigo("COD-001")).thenReturn(List.of(produtoValido()));

            List<ProdutoResponseDto> resultado = produtoService.listarComFiltros(null, "COD-001", null);

            assertEquals(1, resultado.size());
            verify(repository).findByCodigo("COD-001");
            verify(repository, never()).findAll();
        }

        @Test
        @DisplayName("deve filtrar por nome quando código não informado")
        void deveFiltrarPorNome() {
            when(repository.findByNomeContainingIgnoreCase("caneta")).thenReturn(List.of(produtoValido()));

            List<ProdutoResponseDto> resultado = produtoService.listarComFiltros("caneta", null, null);

            assertEquals(1, resultado.size());
            verify(repository).findByNomeContainingIgnoreCase("caneta");
        }

        @Test
        @DisplayName("deve filtrar por categoria quando só categoriaId informado")
        void deveFiltrarPorCategoria() {
            when(repository.findByCategoriaId(2L)).thenReturn(List.of(produtoValido()));

            List<ProdutoResponseDto> resultado = produtoService.listarComFiltros(null, null, 2L);

            assertEquals(1, resultado.size());
            verify(repository).findByCategoriaId(2L);
        }

        @Test
        @DisplayName("deve listar todos quando nenhum filtro informado")
        void deveListarTodosSemFiltro() {
            when(repository.findAll()).thenReturn(List.of(produtoValido(), produtoValido()));

            List<ProdutoResponseDto> resultado = produtoService.listarComFiltros(null, null, null);

            assertEquals(2, resultado.size());
            verify(repository).findAll();
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar o produto quando encontrado")
        void deveRetornarProdutoQuandoEncontrado() {
            Produto produto = new Produto();
            produto.setId(1L);
            produto.setCategoria(categoriaAtiva());
            produto.setUsuario(usuarioLogado());

            when(repository.findById(1L)).thenReturn(Optional.of(produto));

            ProdutoResponseDto dto = produtoService.buscarPorId(1L);

            assertEquals(1L, dto.getId());
        }

        @Test
        @DisplayName("deve lançar ProdutoNaoEncontradoException quando não existir")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(99L));
        }
    }

    @Nested
    @DisplayName("salvarCriar")
    class SalvarCriar {

        @Test
        @DisplayName("deve criar produto e calcular a margem de lucro corretamente")
        void deveCriarProdutoComSucesso() {
            Categoria categoria = categoriaAtiva();
            Usuario usuario = usuarioLogado();

            when(categoriaService.buscarIdCategoria(1L)).thenReturn(categoria);
            when(usuarioService.buscarPorEmail(EMAIL_LOGADO)).thenReturn(usuario);
            when(repository.existsByCodigo("COD-001")).thenReturn(false);

            ProdutoResponseDto resposta = produtoService.salvarCriar(requestDtoValido());

            ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
            verify(repository).save(captor.capture());
            Produto salvo = captor.getValue();

            // custo 50, venda 100 -> lucro 50 -> margem 50.00%
            assertEquals(new BigDecimal("50.00"), salvo.getMargemLucro());
            assertTrue(salvo.getAtivo());
            assertEquals("COD-001", resposta.getCodigo());
        }

        @Test
        @DisplayName("deve lançar CategoriaNaoEncontradaException quando categoria estiver inativa")
        void deveLancarExcecaoQuandoCategoriaInativa() {
            when(categoriaService.buscarIdCategoria(anyLong())).thenReturn(categoriaInativa());
            when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuarioLogado());

            assertThrows(CategoriaNaoEncontradaException.class,
                    () -> produtoService.salvarCriar(requestDtoValido()));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ProdutoJaCadastradoException quando código já existir")
        void deveLancarExcecaoQuandoCodigoJaExiste() {
            when(categoriaService.buscarIdCategoria(anyLong())).thenReturn(categoriaAtiva());
            when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuarioLogado());
            when(repository.existsByCodigo("COD-001")).thenReturn(true);

            assertThrows(ProdutoJaCadastradoException.class,
                    () -> produtoService.salvarCriar(requestDtoValido()));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ProdutoPrejuizoException quando preço de venda <= preço de custo")
        void deveLancarExcecaoQuandoPrecoVendaMenorOuIgualCusto() {
            when(categoriaService.buscarIdCategoria(anyLong())).thenReturn(categoriaAtiva());
            when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuarioLogado());
            when(repository.existsByCodigo(anyString())).thenReturn(false);

            ProdutoRequestDto dto = requestDtoValido();
            dto.setPrecoVenda(new BigDecimal("40.00")); // menor que o custo (50.00)

            assertThrows(ProdutoPrejuizoException.class, () -> produtoService.salvarCriar(dto));

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("salvarAtualizar")
    class SalvarAtualizar {

        @Test
        @DisplayName("deve atualizar produto existente com sucesso")
        void deveAtualizarComSucesso() {
            Produto produtoExistente = new Produto();
            produtoExistente.setId(1L);
            produtoExistente.setCategoria(categoriaAtiva());
            produtoExistente.setUsuario(usuarioLogado());

            when(repository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(categoriaService.buscarIdCategoria(anyLong())).thenReturn(categoriaAtiva());
            when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuarioLogado());

            ProdutoResponseDto resposta = produtoService.salvarAtualizar(1L, requestDtoValido());

            verify(repository).save(produtoExistente);
            assertEquals("COD-001", resposta.getCodigo());
            assertEquals(new BigDecimal("50.00"), produtoExistente.getMargemLucro());
        }

        @Test
        @DisplayName("deve lançar ProdutoNaoEncontradoException quando produto não existir")
        void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ProdutoNaoEncontradoException.class,
                    () -> produtoService.salvarAtualizar(1L, requestDtoValido()));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar CategoriaNaoEncontradaException quando categoria estiver inativa")
        void deveLancarExcecaoQuandoCategoriaInativa() {
            Produto produtoExistente = new Produto();
            produtoExistente.setId(1L);

            when(repository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(categoriaService.buscarIdCategoria(anyLong())).thenReturn(categoriaInativa());
            when(usuarioService.buscarPorEmail(anyString())).thenReturn(usuarioLogado());

            assertThrows(CategoriaNaoEncontradaException.class,
                    () -> produtoService.salvarAtualizar(1L, requestDtoValido()));

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("alterarAtividade")
    class AlterarAtividade {

        @Test
        @DisplayName("deve inverter o status ativo do produto")
        void deveInverterStatusAtivo() {
            Produto produto = new Produto();
            produto.setId(1L);
            produto.setAtivo(true);
            produto.setCategoria(categoriaAtiva());
            produto.setUsuario(usuarioLogado());

            when(repository.findById(1L)).thenReturn(Optional.of(produto));

            ProdutoResponseDto resposta = produtoService.alterarAtividade(1L);

            assertFalse(resposta.getAtivo());
            verify(repository, times(1)).save(produto);
        }

        @Test
        @DisplayName("deve lançar ProdutoNaoEncontradoException quando produto não existir")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.alterarAtividade(1L));
        }
    }
}