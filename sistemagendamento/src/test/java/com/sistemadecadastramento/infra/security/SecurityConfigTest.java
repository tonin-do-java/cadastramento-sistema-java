package com.sistemadecadastramento.infra.security;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.UsuarioRepository;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
@DisplayName("SecurityConfig - matriz de autorização (integração)")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setEmail("admin@email.com");
        admin.setSenhaHash(passwordEncoder.encode("senha1234"));
        admin.setRole(Roles.ADMIN);
        repository.saveAndFlush(admin);
        tokenAdmin = tokenService.gerarToken(admin);

        Usuario user = new Usuario();
        user.setNome("Usuario Comum");
        user.setEmail("user@email.com");
        user.setSenhaHash(passwordEncoder.encode("senha1234"));
        user.setRole(Roles.USER);
        repository.saveAndFlush(user);
        tokenUser = tokenService.gerarToken(user);
    }

    // ---------- Rotas públicas (permitAll) ----------

    @Test
    @DisplayName("POST /api/usuarios deve ser público (permitAll), mesmo sem token")
    void criarUsuario_DeveSerPublico() throws Exception {
        String body = "{"
                + "\"nome\":\"Novo\","
                + "\"email\":\"novo@email.com\","
                + "\"senha\":\"senha1234\","
                + "\"confirmacaoSenha\":\"senha1234\""
                + "}";

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/auth/login deve ser público (permitAll), mesmo sem token")
    void login_DeveSerPublico() throws Exception {
        String body = "{\"email\":\"admin@email.com\",\"senha\":\"senha1234\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Documentação OpenAPI (/v3/api-docs) deve ser pública")
    void apiDocs_DeveSerPublico() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs")).andReturn();

        int status = result.getResponse().getStatus();
        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }

    // ---------- Regra geral: anyRequest().hasRole("ADMIN") ----------

    @Test
    @DisplayName("Sem token, qualquer outra rota deve ser negada (401 ou 403)")
    void semToken_DeveSerNegado() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/usuarios")).andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status == 401 || status == 403, "Esperava 401/403 mas veio " + status);
    }

    @Test
    @DisplayName("Com token de usuário sem role ADMIN, deve ser negado (403)")
    void tokenDeUsuarioComum_DeveSerNegado() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer " + tokenUser)).andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status == 401 || status == 403, "Esperava 401/403 mas veio " + status);
    }

    @Test
    @DisplayName("Com token de ADMIN, GET /api/usuarios deve ser permitido (200)")
    void tokenDeAdmin_DeveSerPermitido() throws Exception {
        mockMvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Com token de ADMIN, DELETE deve ser permitido")
    void tokenDeAdmin_DeveConseguirDeletar() throws Exception {
        Usuario alvo = new Usuario();
        alvo.setNome("Alvo");
        alvo.setEmail("alvo@email.com");
        alvo.setSenhaHash(passwordEncoder.encode("senha1234"));
        alvo.setRole(Roles.USER);
        repository.saveAndFlush(alvo);

        mockMvc.perform(delete("/api/usuarios/{id}", alvo.getId())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Com token de usuário comum, DELETE deve ser negado")
    void tokenDeUsuarioComum_NaoDeveConseguirDeletar() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/usuarios/999")
                .header("Authorization", "Bearer " + tokenUser)).andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status == 401 || status == 403, "Esperava 401/403 mas veio " + status);
    }

    @Test
    @DisplayName("Token com assinatura inválida deve ser negado (401/403), não gerar erro 500")
    void tokenComAssinaturaInvalida_DeveSerNegado() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer token.assinado.errado")).andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status == 401 || status == 403, "Esperava 401/403 mas veio " + status);
    }
}