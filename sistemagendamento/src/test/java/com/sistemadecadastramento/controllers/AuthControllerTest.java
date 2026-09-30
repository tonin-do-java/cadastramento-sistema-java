package com.sistemadecadastramento.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemadecadastramento.dtos.DadosLoginDto;
import com.sistemadecadastramento.dtos.SenhaRequestDto;
import com.sistemadecadastramento.infra.security.TokenService;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.UsuarioService;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - testes unitários (MockMvc standalone)")
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    private Usuario criarUsuario(String email) {
        Usuario u = new Usuario();
        u.setNome("Usuario Teste");
        u.setEmail(email);
        u.setRole(Roles.ADMIN);
        return u;
    }

    // ---------- POST /api/auth/login ----------

    @Test
    @DisplayName("Login com credenciais corretas deve retornar 200 e o token")
    void login_DeveRetornarToken_QuandoCredenciaisCorretas() throws Exception {
        DadosLoginDto loginDto = new DadosLoginDto("admin@email.com", "senha123");

        Authentication authMock = org.mockito.Mockito.mock(Authentication.class);
        Usuario usuarioMock = criarUsuario("admin@email.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(usuarioMock);
        when(tokenService.gerarToken(usuarioMock)).thenReturn("token-jwt-ficticio");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-ficticio"));
    }

    @Test
    @DisplayName("Login deve autenticar com o email e a senha exatos enviados no corpo")
    void login_DeveAutenticarComCredenciaisDoCorpo() throws Exception {
        DadosLoginDto loginDto = new DadosLoginDto("user@email.com", "minhaSenha1");
        Authentication authMock = org.mockito.Mockito.mock(Authentication.class);
        Usuario usuarioMock = criarUsuario("user@email.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(usuarioMock);
        when(tokenService.gerarToken(usuarioMock)).thenReturn("qualquer-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        Assertions.assertEquals("user@email.com", captor.getValue().getPrincipal());
        Assertions.assertEquals("minhaSenha1", captor.getValue().getCredentials());
    }

    @Test
    @DisplayName("Login com senha incorreta deve propagar BadCredentialsException (sem gerar token)")
    void login_DeveFalhar_QuandoSenhaIncorreta() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Usuário ou senha inválidos"));

        String jsonBody = "{\"email\":\"admin@email.com\",\"senha\":\"senha_errada\"}";

        Exception ex = Assertions.assertThrows(Exception.class, () -> mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)));

        Throwable causaRaiz = raizDaCausa(ex);
        Assertions.assertInstanceOf(BadCredentialsException.class, causaRaiz);
        verify(tokenService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Login com usuário inexistente deve propagar BadCredentialsException")
    void login_DeveFalhar_QuandoUsuarioNaoExiste() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Usuário ou senha inválidos"));

        String jsonBody = "{\"email\":\"nao_existo@email.com\",\"senha\":\"senha123\"}";

        Assertions.assertThrows(Exception.class, () -> mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)));

        verify(tokenService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Login com email em branco deve retornar 400 (Bean Validation) sem chamar o AuthenticationManager")
    void login_DeveRetornar400_QuandoEmailEmBranco() throws Exception {
        String jsonBody = "{\"email\":\"\",\"senha\":\"senha123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Login com email em formato inválido deve retornar 400")
    void login_DeveRetornar400_QuandoEmailFormatoInvalido() throws Exception {
        String jsonBody = "{\"email\":\"sem-arroba\",\"senha\":\"senha123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Login com senha curta (menos de 8 caracteres) deve retornar 400")
    void login_DeveRetornar400_QuandoSenhaCurta() throws Exception {
        String jsonBody = "{\"email\":\"admin@email.com\",\"senha\":\"123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    // ---------- POST /api/auth/esqueciSenha ----------
    // ATENÇÃO: assumindo que SenhaRequestDto tem os campos "senha" e "confirmacaoSenha"
    // com os respectivos setters, conforme uso em UsuarioService.alterarSenha. Ajustar
    // este teste se a classe real tiver um formato diferente.

    private SenhaRequestDto criarSenhaRequestDto(String senha, String confirmacao) {
        SenhaRequestDto dto = new SenhaRequestDto();
        dto.setSenha(senha);
        dto.setConfirmacaoSenha(confirmacao);
        return dto;
    }

    @Test
    @DisplayName("esqueciSenha com dados válidos deve retornar 200 e delegar ao UsuarioService")
    void esqueciSenha_DeveRetornarOk() throws Exception {
        SenhaRequestDto dto = criarSenhaRequestDto("novaSenha123", "novaSenha123");

        mockMvc.perform(post("/api/auth/esqueciSenha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).alterarSenha(any(SenhaRequestDto.class));
    }

    // ---------- helper ----------

    private Throwable raizDaCausa(Throwable t) {
        Throwable atual = t;
        while (atual.getCause() != null && atual.getCause() != atual) {
            atual = atual.getCause();
        }
        return atual;
    }
}