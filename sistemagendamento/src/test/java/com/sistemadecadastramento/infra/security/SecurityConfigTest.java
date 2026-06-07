package com.sistemadecadastramento.infra.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.sistemadecadastramento.controllers.UsuarioController;
import com.sistemadecadastramento.dtos.UsuarioCreateRequestDto;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {
    
    private MockMvc mockMvc;

    @Mock
    private TokenService tokenService;

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        
        this.mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }


    @Test
    @DisplayName("Deve permitir o cadastro de usuário anonimamente sem travar o contexto")
    void devePermitirCadastrarUsuarioSemAutenticacao() throws Exception {
        String jsonCompletoDto = "{"
            + "\"nome\":\"Natan\","
            + "\"email\":\"natan@email.com\","
            + "\"senha\":\"13345678\","
            + "\"confirmacaoSenha\":\"13345678\""
            + "}";

        Usuario usuarioMockado = new Usuario();
        usuarioMockado.setId(10L);
        usuarioMockado.setNome("Natan");
        usuarioMockado.setEmail("natan@email.com");

    
        when(usuarioService.transformarDto(any(UsuarioCreateRequestDto.class))).thenReturn(usuarioMockado);
        when(usuarioService.salvarCriar(any(Usuario.class))).thenReturn(usuarioMockado);

        mockMvc.perform(post("/api/usuarios/Cadastrar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCompletoDto))
            .andExpect(status().isCreated());
    }
    
    @Test
    @DisplayName("Garante que rotas de listagem respondem corretamente quando isoladas")
    void deveResponderOkNaListagemQuandoIsolado() throws Exception {
        mockMvc.perform(get("/api/usuarios/listarTodos"))
                .andExpect(status().isOk());
    }

}
