package com.sistemadecadastramento.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.UsuarioService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioController - testes unitários (MockMvc standalone)")
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Usuario criarUsuario(Long id, String nome, String email, Roles role) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome(nome);
        u.setEmail(email);
        u.setRole(role);
        return u;
    }

    private UsuarioCreateRequestDto criarDtoCriacao() {
        UsuarioCreateRequestDto dto = new UsuarioCreateRequestDto();
        dto.setNome("Novo Usuario");
        dto.setEmail("novo@email.com");
        dto.setSenha("senha1234");
        dto.setConfirmacaoSenha("senha1234");
        dto.setRole(Roles.USER);
        return dto;
    }

    // ---------- GET /api/usuarios ----------

    @Test
    @DisplayName("GET /api/usuarios deve retornar 200 e a lista de usuários")
    void listarTodos_DeveRetornarOk() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(
                new UsuarioResponseDto(criarUsuario(1L, "Natan", "natan@email.com", Roles.ADMIN))));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Natan"))
                .andExpect(jsonPath("$[0].email").value("natan@email.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /api/usuarios deve retornar 200 e lista vazia quando não há usuários")
    void listarTodos_DeveRetornarListaVazia() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ---------- GET /api/usuarios/perfil ----------

    @Test
    @DisplayName("GET /api/usuarios/perfil deve retornar 200 com o perfil do usuário logado")
    void buscarMeuPerfil_DeveRetornarOk() throws Exception {
        when(service.buscarMeuPerfil())
                .thenReturn(new UsuarioResponseDto(criarUsuario(3L, "Logado", "logado@email.com", Roles.USER)));

        mockMvc.perform(get("/api/usuarios/perfil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("logado@email.com"));

        // garante que "perfil" não foi interpretado como {id}
        verify(service).buscarMeuPerfil();
    }

    // ---------- GET /api/usuarios/{id} ----------

    @Test
    @DisplayName("GET /api/usuarios/{id} deve retornar 200 e o usuário")
    void buscarPorId_DeveRetornarOk() throws Exception {
        when(service.buscarPorId(1L))
                .thenReturn(new UsuarioResponseDto(criarUsuario(1L, "Natan", "natan@email.com", Roles.USER)));

        mockMvc.perform(get("/api/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Natan"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} com ID não numérico deve retornar 400")
    void buscarPorId_DeveRetornar400_QuandoIdNaoNumerico() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ---------- POST /api/usuarios ----------

    @Test
    @DisplayName("POST /api/usuarios deve retornar 201, Location e corpo quando os dados são válidos")
    void criarUsuario_DeveRetornarCreated() throws Exception {
        Usuario salvo = criarUsuario(10L, "Novo Usuario", "novo@email.com", Roles.USER);
        when(service.transformarDto(any(UsuarioCreateRequestDto.class))).thenReturn(salvo);
        when(service.salvarCriar(any(Usuario.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDtoCriacao())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/usuarios/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Novo Usuario"))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/usuarios deve retornar 400 quando o nome está em branco")
    void criarUsuario_DeveRetornar400_QuandoNomeEmBranco() throws Exception {
        UsuarioCreateRequestDto dto = criarDtoCriacao();
        dto.setNome(" ");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/usuarios deve retornar 400 quando o e-mail é inválido")
    void criarUsuario_DeveRetornar400_QuandoEmailInvalido() throws Exception {
        UsuarioCreateRequestDto dto = criarDtoCriacao();
        dto.setEmail("email-sem-arroba");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/usuarios deve retornar 400 quando a senha tem menos de 8 caracteres")
    void criarUsuario_DeveRetornar400_QuandoSenhaCurta() throws Exception {
        UsuarioCreateRequestDto dto = criarDtoCriacao();
        dto.setSenha("123");
        dto.setConfirmacaoSenha("123");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/usuarios deve retornar 400 quando o corpo está ausente")
    void criarUsuario_DeveRetornar400_QuandoSemCorpo() throws Exception {
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ---------- PUT /api/usuarios/{id} ----------

    @Test
    @DisplayName("PUT /api/usuarios/{id} deve retornar 200 e o usuário atualizado")
    void atualizarUsuario_DeveRetornarOk() throws Exception {
        UsuarioRequestDto dto = new UsuarioRequestDto("Nome Atualizado", "atualizado@email.com", Roles.ESTOQUISTA);
        when(service.salvarAtualizar(eq(1L), any(UsuarioRequestDto.class)))
                .thenReturn(new UsuarioResponseDto(
                        criarUsuario(1L, "Nome Atualizado", "atualizado@email.com", Roles.ESTOQUISTA)));

        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"))
                .andExpect(jsonPath("$.role").value("ESTOQUISTA"));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} deve retornar 400 quando o e-mail é inválido")
    void atualizarUsuario_DeveRetornar400_QuandoEmailInvalido() throws Exception {
        UsuarioRequestDto dto = new UsuarioRequestDto("Nome", "invalido", Roles.USER);

        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ---------- PUT /api/usuarios/perfil ----------

    @Test
    @DisplayName("PUT /api/usuarios/perfil deve retornar 200 com o perfil alterado")
    void alterarMeuPerfil_DeveRetornarOk() throws Exception {
        UsuarioRequestDto dto = new UsuarioRequestDto("Meu Novo Nome", "meu@email.com", null);
        when(service.alterarMeuPerfil(any(UsuarioRequestDto.class)))
                .thenReturn(new UsuarioResponseDto(criarUsuario(3L, "Meu Novo Nome", "meu@email.com", Roles.USER)));

        mockMvc.perform(put("/api/usuarios/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Meu Novo Nome"));

        verify(service).alterarMeuPerfil(any(UsuarioRequestDto.class));
    }

    @Test
    @DisplayName("PUT /api/usuarios/perfil deve retornar 400 quando o nome está em branco")
    void alterarMeuPerfil_DeveRetornar400_QuandoNomeEmBranco() throws Exception {
        UsuarioRequestDto dto = new UsuarioRequestDto("", "meu@email.com", null);

        mockMvc.perform(put("/api/usuarios/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ---------- DELETE /api/usuarios/{id} ----------

    @Test
    @DisplayName("DELETE /api/usuarios/{id} deve retornar 204 No Content")
    void deletarUsuario_DeveRetornarNoContent() throws Exception {
        doNothing().when(service).deletar(1L);

        mockMvc.perform(delete("/api/usuarios/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deletar(1L);
    }
}