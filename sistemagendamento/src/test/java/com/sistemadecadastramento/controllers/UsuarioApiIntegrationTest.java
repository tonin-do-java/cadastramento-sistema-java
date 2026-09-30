package com.sistemadecadastramento.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadecadastramento.infra.security.TokenService;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.UsuarioRepository;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase // troca o banco real por H2 em memória
@Transactional
@DisplayName("API de Usuários - integração com Spring Security real")
class UsuarioApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario admin;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        admin = criarUsuario("admin@email.com", "Admin", Roles.ADMIN);
        tokenAdmin = "Bearer " + tokenService.gerarToken(admin);
    }

    private Usuario criarUsuario(String email, String nome, Roles role) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenhaHash(passwordEncoder.encode("senha1234"));
        u.setRole(role);
        return repository.saveAndFlush(u);
    }

    @Test
    @DisplayName("Sem token, rota protegida deve ser negada (401 ou 403)")
    void semToken_DeveSerNegado() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/usuarios/perfil")).andReturn();

        int s = result.getResponse().getStatus();
        assertTrue(s == 401 || s == 403, "Esperava 401/403 mas veio " + s);
    }

    @Test
    @DisplayName("Token inválido deve ser negado (401 ou 403)")
    void tokenInvalido_DeveSerNegado() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/usuarios/perfil")
                .header("Authorization", "Bearer token.invalido.aqui")).andReturn();

        int s = result.getResponse().getStatus();
        assertTrue(s == 401 || s == 403, "Esperava 401/403 mas veio " + s);
    }

    @Test
    @DisplayName("Com token válido, GET /perfil deve retornar os dados do dono do token")
    void comToken_DeveRetornarPerfil() throws Exception {
        mockMvc.perform(get("/api/usuarios/perfil").header("Authorization", tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@email.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    @DisplayName("ADMIN deve conseguir listar usuários")
    void admin_DeveListar() throws Exception {
        criarUsuario("outro@email.com", "Outro", Roles.USER);

        mockMvc.perform(get("/api/usuarios").header("Authorization", tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("PUT /perfil deve persistir a alteração no banco")
    void alterarPerfil_DevePersistir() throws Exception {
        String body = "{\"nome\":\"Admin Renomeado\",\"email\":\"admin@email.com\"}";

        mockMvc.perform(put("/api/usuarios/perfil")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Admin Renomeado"));

        assertEquals("Admin Renomeado", repository.findById(admin.getId()).orElseThrow().getNome());
    }

    @Test
    @DisplayName("PUT /perfil com corpo inválido deve retornar 400 e não alterar o banco")
    void alterarPerfil_Invalido_DeveRetornar400() throws Exception {
        String body = "{\"nome\":\"\",\"email\":\"nao-e-email\"}";

        mockMvc.perform(put("/api/usuarios/perfil")
                        .header("Authorization", tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        assertEquals("Admin", repository.findById(admin.getId()).orElseThrow().getNome());
    }

    @Test
    @DisplayName("ADMIN deve conseguir deletar outro usuário (204) e ele some do banco")
    void admin_DeveDeletarUsuario() throws Exception {
        Usuario alvo = criarUsuario("alvo@email.com", "Alvo", Roles.USER);

        mockMvc.perform(delete("/api/usuarios/{id}", alvo.getId()).header("Authorization", tokenAdmin))
                .andExpect(status().isNoContent());

        assertFalse(repository.existsById(alvo.getId()));
    }

    @Test
    @DisplayName("Usuário comum (USER) não deve conseguir deletar outros usuários")
    void usuarioComum_NaoDeveDeletar() throws Exception {
        Usuario comum = criarUsuario("comum@email.com", "Comum", Roles.USER);
        String tokenComum = "Bearer " + tokenService.gerarToken(comum);

        MvcResult result = mockMvc.perform(delete("/api/usuarios/{id}", admin.getId())
                .header("Authorization", tokenComum)).andReturn();

        int s = result.getResponse().getStatus();
        assertTrue(s == 401 || s == 403, "Esperava 401/403 mas veio " + s);
        assertTrue(repository.existsById(admin.getId()));
    }
}