package com.sistemadecadastramento.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.sistemadecadastramento.repository.UsuarioRepository;

/**
 * End-to-end: aplicação completa em porta aleatória, requisições HTTP reais,
 * banco H2, JWT real. Percorre o fluxo:
 * cadastro do 1º usuário (vira ADMIN) -> login -> perfil -> editar perfil ->
 * cadastrar 2º usuário -> listar -> deletar -> confirmar remoção.
 *
 * ATENÇÃO: assume que POST /api/usuarios e POST /api/auth/login são públicos
 * (permitAll na SecurityConfig) e que todas as demais rotas exigem ROLE_ADMIN.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase // nunca usar o banco real em E2E
@DisplayName("Usuários - fluxo end-to-end")
class UsuarioE2ETest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    private HttpEntity<Object> json(Object body, String token) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            h.setBearerAuth(token);
        }
        return new HttpEntity<>(body, h);
    }

    private String login(String email, String senha) {
        // AuthController: @RequestMapping("/api") + @PostMapping("/auth/login") = "/api/auth/login"
        ResponseEntity<Map> resp = rest.postForEntity("/api/auth/login",
                json(Map.of("email", email, "senha", senha), null), Map.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        String token = (String) resp.getBody().get("token");
        assertNotNull(token);
        return token;
    }

    @Test
    @DisplayName("Fluxo completo: cadastro -> login -> perfil -> edição -> 2º usuário -> listagem -> exclusão")
    void fluxoCompletoDeUsuarios() {
        // 1) Cadastro do primeiro usuário: deve virar ADMIN
        Map<String, Object> primeiro = Map.of(
                "nome", "Admin Inicial",
                "email", "admin@email.com",
                "senha", "senha1234",
                "confirmacaoSenha", "senha1234",
                "role", "USER");

        ResponseEntity<Map> criado = rest.postForEntity("/api/usuarios", json(primeiro, null), Map.class);

        assertEquals(HttpStatus.CREATED, criado.getStatusCode());
        assertNotNull(criado.getHeaders().getLocation());
        assertEquals("ADMIN", criado.getBody().get("role"));
        assertTrue(!criado.getBody().containsKey("senha") && !criado.getBody().containsKey("senhaHash"));

        // 2) Login
        String token = login("admin@email.com", "senha1234");

        // 3) Perfil
        ResponseEntity<Map> perfil = rest.exchange("/api/usuarios/perfil", HttpMethod.GET,
                json(null, token), Map.class);
        assertEquals(HttpStatus.OK, perfil.getStatusCode());
        assertEquals("admin@email.com", perfil.getBody().get("email"));

        // 4) Editar o próprio perfil
        ResponseEntity<Map> editado = rest.exchange("/api/usuarios/perfil", HttpMethod.PUT,
                json(Map.of("nome", "Admin Renomeado", "email", "admin@email.com"), token), Map.class);
        assertEquals(HttpStatus.OK, editado.getStatusCode());
        assertEquals("Admin Renomeado", editado.getBody().get("nome"));

        // 5) Cadastrar segundo usuário (como ADMIN)
        Map<String, Object> segundo = Map.of(
                "nome", "Estoquista",
                "email", "estoquista@email.com",
                "senha", "senha1234",
                "confirmacaoSenha", "senha1234",
                "role", "ESTOQUISTA");
        ResponseEntity<Map> segundoCriado = rest.exchange("/api/usuarios", HttpMethod.POST,
                json(segundo, token), Map.class);
        assertEquals(HttpStatus.CREATED, segundoCriado.getStatusCode());
        assertEquals("ESTOQUISTA", segundoCriado.getBody().get("role"));
        Number idSegundo = (Number) segundoCriado.getBody().get("id");

        // 6) Listar
        ResponseEntity<List> lista = rest.exchange("/api/usuarios", HttpMethod.GET, json(null, token), List.class);
        assertEquals(HttpStatus.OK, lista.getStatusCode());
        assertEquals(2, lista.getBody().size());

        // 7) O segundo usuário consegue logar
        assertNotNull(login("estoquista@email.com", "senha1234"));

        // 8) Deletar o segundo usuário
        ResponseEntity<Void> deletado = rest.exchange("/api/usuarios/" + idSegundo, HttpMethod.DELETE,
                json(null, token), Void.class);
        assertEquals(HttpStatus.NO_CONTENT, deletado.getStatusCode());

        // 9) Confirmar que sumiu
        ResponseEntity<String> busca = rest.exchange("/api/usuarios/" + idSegundo, HttpMethod.GET,
                json(null, token), String.class);
        assertTrue(busca.getStatusCode().is4xxClientError(),
                "Esperava erro 4xx ao buscar usuário deletado, veio " + busca.getStatusCode());
    }

    @Test
    @DisplayName("Login com senha errada não deve retornar token")
    void loginComSenhaErrada() {
        Map<String, Object> usuario = Map.of(
                "nome", "Admin", "email", "admin@email.com",
                "senha", "senha1234", "confirmacaoSenha", "senha1234");
        rest.postForEntity("/api/usuarios", json(usuario, null), Map.class);

        ResponseEntity<String> resp = rest.postForEntity("/api/auth/login",
                json(Map.of("email", "admin@email.com", "senha", "senhaErrada"), null), String.class);

        assertTrue(resp.getStatusCode().is4xxClientError() || resp.getStatusCode().is5xxServerError());
        assertTrue(resp.getBody() == null || !resp.getBody().contains("\"token\""));
    }

    @Test
    @DisplayName("Rota protegida sem token deve ser negada")
    void rotaProtegidaSemToken() {
        ResponseEntity<String> resp = rest.getForEntity("/api/usuarios/perfil", String.class);

        assertTrue(resp.getStatusCode() == HttpStatus.UNAUTHORIZED || resp.getStatusCode() == HttpStatus.FORBIDDEN);
    }
}