package com.sistemadecadastramento.infra.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
public class SecurityFilterTest {
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

    @Test
    @DisplayName("Deve autenticar o usuário no contexto do Spring quando o Token JWT for válido")
    void deveAutenticarUsuarioComTokenValido() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        
        request.addHeader("Authorization", "Bearer token_valido_123");

        
        UserDetails usuarioMock = mock(UserDetails.class);
        when(usuarioMock.getAuthorities()).thenReturn(Collections.emptyList());

        
        when(tokenService.validarToken("token_valido_123")).thenReturn("natan@email.com");
        when(autenticacaoService.loadUserByUsername("natan@email.com")).thenReturn(usuarioMock);

        
        securityFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(usuarioMock, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
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
}
