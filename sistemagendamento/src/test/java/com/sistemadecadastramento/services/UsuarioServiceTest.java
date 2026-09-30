package com.sistemadecadastramento.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sistemadecadastramento.dtos.SenhaRequestDto;
import com.sistemadecadastramento.dtos.UsuarioCreateRequestDto;
import com.sistemadecadastramento.dtos.UsuarioRequestDto;
import com.sistemadecadastramento.dtos.UsuarioResponseDto;
import com.sistemadecadastramento.exceptions.CamposIncorretosException;
import com.sistemadecadastramento.exceptions.UsuarioJaCadastradoException;
import com.sistemadecadastramento.exceptions.UsuarioNaoCadastradoException;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - testes unitários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    // ---------- helpers ----------

    private Usuario criarUsuario(Long id, String nome, String email, Roles role) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome(nome);
        u.setEmail(email);
        u.setRole(role);
        return u;
    }

    private UsuarioCreateRequestDto criarDtoCriacao(String senha, String confirmacao, Roles role) {
        UsuarioCreateRequestDto dto = new UsuarioCreateRequestDto();
        dto.setNome("Usuario Teste");
        dto.setEmail("teste@email.com");
        dto.setSenha(senha);
        dto.setConfirmacaoSenha(confirmacao);
        dto.setRole(role);
        return dto;
    }

    private void autenticarComo(String email) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(email, null, List.of()));
        SecurityContextHolder.setContext(ctx);
    }

    // ---------- listarTodos ----------

    @Test
    @DisplayName("listarTodos: deve converter todos os usuários em DTOs")
    void listarTodos_DeveRetornarListaDeDtos() {
        when(repository.findAll()).thenReturn(List.of(
                criarUsuario(1L, "Ana", "ana@email.com", Roles.ADMIN),
                criarUsuario(2L, "Beto", "beto@email.com", Roles.USER)));

        List<UsuarioResponseDto> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNome());
        assertEquals("beto@email.com", resultado.get(1).getEmail());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarTodos: deve retornar lista vazia quando não há usuários")
    void listarTodos_DeveRetornarListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        assertEquals(0, service.listarTodos().size());
    }

    // ---------- buscarPorId ----------

    @Test
    @DisplayName("buscarPorId: deve retornar DTO quando o ID existir")
    void buscarPorId_DeveRetornarDto_QuandoExistir() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarUsuario(1L, "Natan", "natan@email.com", Roles.USER)));

        UsuarioResponseDto resultado = service.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Natan", resultado.getNome());
        assertEquals("natan@email.com", resultado.getEmail());
        assertEquals(Roles.USER, resultado.getRole());
    }

    @Test
    @DisplayName("buscarPorId: deve lançar exceção quando o ID não existir")
    void buscarPorId_DeveLancarExcecao_QuandoNaoExistir() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoCadastradoException.class, () -> service.buscarPorId(999L));
    }

    // ---------- buscarPorEmail ----------

    @Test
    @DisplayName("buscarPorEmail: deve retornar a entidade quando o e-mail existir")
    void buscarPorEmail_DeveRetornarUsuario_QuandoExistir() {
        Usuario usuario = criarUsuario(1L, "Natan", "natan@email.com", Roles.USER);
        when(repository.findByEmail("natan@email.com")).thenReturn(Optional.of(usuario));

        assertSame(usuario, service.buscarPorEmail("natan@email.com"));
    }

    @Test
    @DisplayName("buscarPorEmail: deve lançar exceção quando o e-mail não existir")
    void buscarPorEmail_DeveLancarExcecao_QuandoNaoExistir() {
        when(repository.findByEmail("nada@email.com")).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoCadastradoException.class, () -> service.buscarPorEmail("nada@email.com"));
    }

    // ---------- salvarCriar ----------

    @Test
    @DisplayName("salvarCriar: deve salvar e retornar o usuário quando o e-mail é novo")
    void salvarCriar_DeveSalvar_QuandoEmailNaoExiste() {
        Usuario usuario = criarUsuario(null, "Novo", "novo@email.com", Roles.ADMIN);
        when(repository.existsByEmail("novo@email.com")).thenReturn(false);
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario salvo = service.salvarCriar(usuario);

        assertEquals(Roles.ADMIN, salvo.getRole());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("salvarCriar: deve lançar exceção e não salvar quando o e-mail já existe")
    void salvarCriar_DeveLancarExcecao_QuandoEmailJaExiste() {
        Usuario usuario = criarUsuario(null, "Novo", "existe@email.com", Roles.USER);
        when(repository.existsByEmail("existe@email.com")).thenReturn(true);

        UsuarioJaCadastradoException ex = assertThrows(UsuarioJaCadastradoException.class,
                () -> service.salvarCriar(usuario));

        assertEquals("O usuario que usa esse email, já está cadastrado!", ex.getMessage());
        verify(repository, never()).save(any(Usuario.class));
    }

    // ---------- salvarAtualizar ----------

    @Test
    @DisplayName("salvarAtualizar: deve atualizar nome, e-mail e role")
    void salvarAtualizar_DeveAtualizarCampos() {
        Usuario existente = criarUsuario(1L, "Antigo", "antigo@email.com", Roles.USER);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        UsuarioRequestDto dto = new UsuarioRequestDto("Atualizado", "novo@email.com", Roles.ESTOQUISTA);

        UsuarioResponseDto resposta = service.salvarAtualizar(1L, dto);

        assertEquals("Atualizado", resposta.getNome());
        assertEquals("novo@email.com", resposta.getEmail());
        assertEquals(Roles.ESTOQUISTA, resposta.getRole());
        verify(repository, times(1)).save(existente);
    }

    @Test
    @DisplayName("salvarAtualizar: deve lançar exceção e não salvar quando o ID não existir")
    void salvarAtualizar_DeveLancarExcecao_QuandoIdNaoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        UsuarioRequestDto dto = new UsuarioRequestDto("Nome", "e@email.com", Roles.USER);

        assertThrows(UsuarioNaoCadastradoException.class, () -> service.salvarAtualizar(999L, dto));
        verify(repository, never()).save(any(Usuario.class));
    }

    // ---------- deletar ----------

    @Test
    @DisplayName("deletar: deve remover quando o ID existir")
    void deletar_DeveRemover_QuandoIdExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deletar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deletar: deve lançar exceção e não remover quando o ID não existir")
    void deletar_DeveLancarExcecao_QuandoIdNaoExiste() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThrows(UsuarioNaoCadastradoException.class, () -> service.deletar(999L));
        verify(repository, never()).deleteById(any());
    }

    // ---------- transformarDto ----------

    @Test
    @DisplayName("transformarDto: primeiro usuário do sistema vira ADMIN, ignorando o role do DTO")
    void transformarDto_PrimeiroUsuario_DeveSerAdmin() {
        UsuarioCreateRequestDto dto = criarDtoCriacao("12345678", "12345678", Roles.USER);
        when(repository.count()).thenReturn(0L);
        when(passwordEncoder.encode("12345678")).thenReturn("hash");

        Usuario resultado = service.transformarDto(dto);

        assertEquals(Roles.ADMIN, resultado.getRole());
        assertEquals("hash", resultado.getSenhaHash());
    }

    @Test
    @DisplayName("transformarDto: a partir do segundo usuário, mantém o role informado no DTO")
    void transformarDto_UsuariosSeguintes_DevemManterRoleDoDto() {
        UsuarioCreateRequestDto dto = criarDtoCriacao("12345678", "12345678", Roles.ESTOQUISTA);
        when(repository.count()).thenReturn(1L);
        when(passwordEncoder.encode("12345678")).thenReturn("hash");

        Usuario resultado = service.transformarDto(dto);

        assertEquals(Roles.ESTOQUISTA, resultado.getRole());
    }

    @Test
    @DisplayName("transformarDto: deve copiar nome e e-mail e nunca guardar a senha em texto puro")
    void transformarDto_DeveCopiarDadosECriptografarSenha() {
        UsuarioCreateRequestDto dto = criarDtoCriacao("12345678", "12345678", Roles.USER);
        when(repository.count()).thenReturn(5L);
        when(passwordEncoder.encode("12345678")).thenReturn("$2a$hash");

        Usuario resultado = service.transformarDto(dto);

        assertEquals("Usuario Teste", resultado.getNome());
        assertEquals("teste@email.com", resultado.getEmail());
        assertNotEquals("12345678", resultado.getSenhaHash());
        verify(passwordEncoder, times(1)).encode("12345678");
    }

    @Test
    @DisplayName("transformarDto: deve lançar exceção quando senha e confirmação são diferentes")
    void transformarDto_DeveLancarExcecao_QuandoSenhasDiferentes() {
        UsuarioCreateRequestDto dto = criarDtoCriacao("12345678", "outra-senha", Roles.USER);

        CamposIncorretosException ex = assertThrows(CamposIncorretosException.class,
                () -> service.transformarDto(dto));

        assertEquals("Os campos estão incorretos!", ex.getMessage());
        verifyNoInteractions(passwordEncoder);
    }

    // ---------- alterarSenha ----------

    @Test
    @DisplayName("alterarSenha: deve salvar o novo hash do usuário logado")
    void alterarSenha_DeveSalvarNovoHash() {
        autenticarComo("logado@email.com");
        Usuario logado = criarUsuario(1L, "Logado", "logado@email.com", Roles.USER);
        when(repository.findByEmail("logado@email.com")).thenReturn(Optional.of(logado));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novo-hash");

        SenhaRequestDto dto = new SenhaRequestDto();
        dto.setSenha("novaSenha123");
        dto.setConfirmacaoSenha("novaSenha123");

        service.alterarSenha(dto);

        assertEquals("novo-hash", logado.getSenhaHash());
        verify(repository, times(1)).save(logado);
    }

    @Test
    @DisplayName("alterarSenha: deve lançar exceção e não salvar quando as senhas divergem")
    void alterarSenha_DeveLancarExcecao_QuandoSenhasDiferentes() {
        autenticarComo("logado@email.com");
        when(repository.findByEmail("logado@email.com"))
                .thenReturn(Optional.of(criarUsuario(1L, "Logado", "logado@email.com", Roles.USER)));

        SenhaRequestDto dto = new SenhaRequestDto();
        dto.setSenha("novaSenha123");
        dto.setConfirmacaoSenha("diferente123");

        assertThrows(CamposIncorretosException.class, () -> service.alterarSenha(dto));
        verify(repository, never()).save(any(Usuario.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("alterarSenha: deve lançar exceção quando o usuário logado não existe mais")
    void alterarSenha_DeveLancarExcecao_QuandoUsuarioLogadoNaoExiste() {
        autenticarComo("fantasma@email.com");
        when(repository.findByEmail("fantasma@email.com")).thenReturn(Optional.empty());

        SenhaRequestDto dto = new SenhaRequestDto();
        dto.setSenha("novaSenha123");
        dto.setConfirmacaoSenha("novaSenha123");

        assertThrows(UsuarioNaoCadastradoException.class, () -> service.alterarSenha(dto));
        verify(repository, never()).save(any(Usuario.class));
    }

    // ---------- alterarMeuPerfil ----------

    @Test
    @DisplayName("alterarMeuPerfil: deve atualizar nome e e-mail do usuário logado, sem mexer no role")
    void alterarMeuPerfil_DeveAtualizarNomeEEmail() {
        autenticarComo("logado@email.com");
        Usuario logado = criarUsuario(1L, "Antigo", "logado@email.com", Roles.ESTOQUISTA);
        when(repository.findByEmail("logado@email.com")).thenReturn(Optional.of(logado));

        UsuarioRequestDto dto = new UsuarioRequestDto("Novo Nome", "novo@email.com", Roles.ADMIN);

        UsuarioResponseDto resposta = service.alterarMeuPerfil(dto);

        assertEquals("Novo Nome", resposta.getNome());
        assertEquals("novo@email.com", resposta.getEmail());
        // segurança: o usuário não pode se promover pelo próprio perfil
        assertEquals(Roles.ESTOQUISTA, resposta.getRole());
        verify(repository, times(1)).save(logado);
    }

    @Test
    @DisplayName("alterarMeuPerfil: deve lançar exceção quando o usuário logado não existe")
    void alterarMeuPerfil_DeveLancarExcecao_QuandoUsuarioNaoExiste() {
        autenticarComo("fantasma@email.com");
        when(repository.findByEmail("fantasma@email.com")).thenReturn(Optional.empty());

        UsuarioRequestDto dto = new UsuarioRequestDto("Nome", "e@email.com", Roles.USER);

        assertThrows(UsuarioNaoCadastradoException.class, () -> service.alterarMeuPerfil(dto));
        verify(repository, never()).save(any(Usuario.class));
    }

    // ---------- buscarMeuPerfil ----------

    @Test
    @DisplayName("buscarMeuPerfil: deve retornar os dados do usuário autenticado")
    void buscarMeuPerfil_DeveRetornarUsuarioLogado() {
        autenticarComo("logado@email.com");
        when(repository.findByEmail("logado@email.com"))
                .thenReturn(Optional.of(criarUsuario(7L, "Logado", "logado@email.com", Roles.ADMIN)));

        UsuarioResponseDto resposta = service.buscarMeuPerfil();

        assertNotNull(resposta);
        assertEquals(7L, resposta.getId());
        assertEquals("logado@email.com", resposta.getEmail());
        assertEquals(Roles.ADMIN, resposta.getRole());
    }

    @Test
    @DisplayName("buscarMeuPerfil: deve lançar exceção quando o usuário autenticado não existe")
    void buscarMeuPerfil_DeveLancarExcecao_QuandoNaoExiste() {
        autenticarComo("fantasma@email.com");
        when(repository.findByEmail("fantasma@email.com")).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoCadastradoException.class, () -> service.buscarMeuPerfil());
    }
}