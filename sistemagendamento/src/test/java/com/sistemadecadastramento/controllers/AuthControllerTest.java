package com.sistemadecadastramento.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import jakarta.servlet.ServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemadecadastramento.dtos.DadosLoginDto;
import com.sistemadecadastramento.infra.security.TokenService;
import com.sistemadecadastramento.models.Usuario;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthController authController; 

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("Deve retornar HTTP 200 e o Token JWT quando as credenciais estiverem corretas")
    void deveRetornarTokenComCredenciaisCorretas() throws Exception {
        // ARRANGE
        DadosLoginDto loginDto = new DadosLoginDto("admin@email.com", "senha123");
        
        Authentication authMock = org.mockito.Mockito.mock(Authentication.class);
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("admin@email.com");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(usuarioMock);
        when(tokenService.gerarToken(any(Usuario.class))).thenReturn("token-jwt-ficticio");

        // ACT & ASSERT
        mockMvc.perform(post("/login") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-ficticio"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 403 (Forbidden) quando a senha estiver errada")
    void deveRetornarErroComSenhaIncorreta() throws Exception {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Usuário ou senha inválidos"));

        
        String jsonSimples = "{\"email\":\"admin@email.com\",\"senha\":\"senha_errada\"}";

    
        org.junit.jupiter.api.Assertions.assertThrows(ServletException.class, () -> {
                mockMvc.perform(post("/login")
                                .contentType("application/json")
                                .content(jsonSimples));
        });
    }

    @Test
    @DisplayName("Deve retornar HTTP 403 (Forbidden) quando o usuário não existir")
    void deveRetornarErroComUsuarioInexistente() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Usuário ou senha inválidos"));

        String jsonSimples = "{\"email\":\"nao_existo@email.com\",\"senha\":\"senha123\"}";

        
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/login")
                            .contentType("application/json")
                            .content(jsonSimples));
        });
    }
}