package com.sistemadecadastramento.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemadecadastramento.dtos.UsuarioCreateRequestDto;
import com.sistemadecadastramento.dtos.UsuarioRequestDto;
import com.sistemadecadastramento.dtos.UsuarioResponseDto;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        
        this.mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    @DisplayName("Deve retornar lista de usuários e status 200 ao listar todos")
    void deveListarTodosUsuarios() throws Exception {
    
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Natan");
        usuarioMock.setEmail("natan@email.com");
    
   
        UsuarioResponseDto dto = new UsuarioResponseDto(usuarioMock);
    
        List<UsuarioResponseDto> lista = List.of(dto);
        when(service.listarTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/usuarios/listarTodos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Natan"))
                .andExpect(jsonPath("$[0].email").value("natan@email.com"));
    }

    @Test
    @DisplayName("Deve retornar usuário e status 200 ao buscar por ID existente")
    void deveBuscarPorIdComSucesso() throws Exception {
    
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Natan");
    
        UsuarioResponseDto dto = new UsuarioResponseDto(usuarioMock);
    
        when(service.buscarPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/usuarios/listarPorId/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Natan"));
    }

    @Test
    @DisplayName("Deve atualizar usuário e retornar status 200 ao passar dados válidos")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        UsuarioRequestDto requestDto = new UsuarioRequestDto();
        requestDto.setNome("Nome Atualizado");
        requestDto.setEmail("natan.atualizado@email.com");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Nome Atualizado");
        usuarioMock.setEmail("natan.atualizado@email.com");

    
        UsuarioResponseDto respostaDto = new UsuarioResponseDto(usuarioMock);

        when(service.salvarAtualizar(eq(1L), any(UsuarioRequestDto.class))).thenReturn(respostaDto);

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/usuarios/AtualizarCadastro/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"));
    }

    @Test
    @DisplayName("Deve cadastrar usuário e retornar status 201 Created com a URI no Header")
    void deveCriarUsuarioComSucesso() throws Exception {
    
        UsuarioCreateRequestDto requestDto = new UsuarioCreateRequestDto();
        requestDto.setNome("Novo Usuario");
        requestDto.setEmail("novo@email.com");
        requestDto.setSenha("senha123");
        requestDto.setConfirmacaoSenha("senha123");

    
        Usuario usuarioMockado = new Usuario();
        usuarioMockado.setId(10L); // O banco gerou o ID 10
        usuarioMockado.setNome("Novo Usuario");
        usuarioMockado.setEmail("novo@email.com");

    
        when(service.transformarDto(any(UsuarioCreateRequestDto.class))).thenReturn(usuarioMockado);
        when(service.salvarCriar(any(Usuario.class))).thenReturn(usuarioMockado);

    
        String jsonBody = objectMapper.writeValueAsString(requestDto);

    
        mockMvc.perform(post("/api/usuarios/Cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location")) // Garante que o link da URI foi gerado no Header
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("Novo Usuario"));
    }

    @Test
    @DisplayName("Deve deletar usuário e retornar status 204 No Content")
    void deveDeletarUsuarioComSucesso() throws Exception {
    
        doNothing().when(service).deletar(1L);

    
        mockMvc.perform(delete("/api/usuarios/deletarUsuario/{id}", 1L))
                .andExpect(status().isNoContent()); // Valida o retorno HTTP 204
    }
}
