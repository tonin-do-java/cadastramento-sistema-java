package com.sistemadecadastramento.infra.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityFilter - testes unitários")
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limpar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar o usuário no contexto do Spring quando o token JWT for válido")
    void deveAutenticarUsuarioComTokenValido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer token_valido_123");

        UserDetails usuarioMock = mock(UserDetails.class);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(usuarioMock.getAuthorities()).thenAnswer(inv -> authorities);

        when(tokenService.validarToken("token_valido_123")).thenReturn("natan@email.com");
        when(autenticacaoService.loadUserByUsername("natan@email.com")).thenReturn(usuarioMock);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(usuarioMock, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(authorities, SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar o usuário se o cabeçalho Authorization estiver ausente")
    void naoDeveAutenticarSemToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(tokenService, autenticacaoService);
    }

    @Test
    @DisplayName("Não deve autenticar se o token for inválido ou estiver expirado")
    void naoDeveAutenticarComTokenInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer token_expirado");

        when(tokenService.validarToken("token_expirado")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(autenticacaoService);
    }

    @Test
    @DisplayName("Header 'Authorization: Bearer ' (sem token) não deve chamar o TokenService nem autenticar")
    void authorizationComTokenVazio_NaoDeveChamarTokenService() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer ");

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(tokenService, autenticacaoService);
    }

    @Test
    @DisplayName("Cabeçalho Authorization sem o prefixo 'Bearer ' deve ser tratado como o próprio token")
    void authorizationSemPrefixoBearer_DeveUsarValorComoToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // sem "Bearer ", recuperarToken() só remove o prefixo se ele existir
        request.addHeader("Authorization", "token_sem_prefixo");

        when(tokenService.validarToken("token_sem_prefixo")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(tokenService, times(1)).validarToken("token_sem_prefixo");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve sempre continuar a cadeia de filtros (doFilter), mesmo sem autenticação")
    void deveSempreContinuarCadeia() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Depois da correção de SecurityFilter (try/catch em torno de
     * tokenService.validarToken), um token malformado/inválido não deve mais
     * lançar exceção: o filtro apenas segue sem autenticar, e quem barra a
     * requisição é a regra de autorização da SecurityConfig.
     */
    @Test
    @DisplayName("Token malformado não deve autenticar nem lançar exceção (após a correção)")
    void tokenInvalido_NaoDeveAutenticarNemLancarExcecao() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer token-malformado");

        when(tokenService.validarToken("token-malformado"))
                .thenThrow(new io.jsonwebtoken.MalformedJwtException("token malformado"));

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(autenticacaoService);
    }

    @Test
    @DisplayName("Não deve autenticar quando o usuário do token não existir mais (delegado ao service)")
    void tokenValidoMasUsuarioNaoEncontrado_DevePropagarExcecaoDoService() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer token_valido");

        when(tokenService.validarToken("token_valido")).thenReturn("fantasma@email.com");
        when(autenticacaoService.loadUserByUsername("fantasma@email.com"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("não encontrado"));

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> securityFilter.doFilterInternal(request, response, filterChain));
    }
}