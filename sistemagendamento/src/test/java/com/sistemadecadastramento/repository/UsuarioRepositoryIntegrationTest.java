package com.sistemadecadastramento.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;

/**
 * Integração: JPA + banco em memória (H2) + UsuarioRepository real.
 * Requer H2 no classpath de teste.
 */
@DataJpaTest
@DisplayName("UsuarioRepository - integração com banco H2")
class UsuarioRepositoryIntegrationTest {

    @Autowired
    private UsuarioRepository repository;

    private Usuario novoUsuario(String email) {
        Usuario u = new Usuario();
        u.setNome("Usuario " + email);
        u.setEmail(email);
        u.setSenhaHash("hash");
        u.setRole(Roles.USER);
        return u;
    }

    @Test
    @DisplayName("save deve gerar ID e persistir todos os campos")
    void save_DevePersistir() {
        Usuario salvo = repository.saveAndFlush(novoUsuario("a@email.com"));

        assertNotNull(salvo.getId());
        Usuario lido = repository.findById(salvo.getId()).orElseThrow();
        assertEquals("a@email.com", lido.getEmail());
        assertEquals(Roles.USER, lido.getRole());
        assertEquals("hash", lido.getSenhaHash());
    }

    @Test
    @DisplayName("findByEmail deve retornar o usuário quando existir")
    void findByEmail_Existente() {
        repository.saveAndFlush(novoUsuario("busca@email.com"));

        Optional<Usuario> resultado = repository.findByEmail("busca@email.com");

        assertTrue(resultado.isPresent());
        assertEquals("busca@email.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("findByEmail deve retornar vazio quando não existir")
    void findByEmail_Inexistente() {
        assertTrue(repository.findByEmail("nao-existe@email.com").isEmpty());
    }

    @Test
    @DisplayName("existsByEmail deve refletir a existência do e-mail")
    void existsByEmail() {
        repository.saveAndFlush(novoUsuario("existe@email.com"));

        assertTrue(repository.existsByEmail("existe@email.com"));
        assertFalse(repository.existsByEmail("outro@email.com"));
    }

    @Test
    @DisplayName("e-mail duplicado deve violar a constraint unique")
    void emailDuplicado_DeveFalhar() {
        repository.saveAndFlush(novoUsuario("dup@email.com"));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(novoUsuario("dup@email.com")));
    }

    @Test
    @DisplayName("nome nulo deve violar nullable=false")
    void nomeNulo_DeveFalhar() {
        Usuario u = novoUsuario("semnome@email.com");
        u.setNome(null);

        assertThrows(Exception.class, () -> repository.saveAndFlush(u));
    }

    @Test
    @DisplayName("count, deleteById e existsById devem funcionar em conjunto")
    void countEDelete() {
        long antes = repository.count();
        Usuario salvo = repository.saveAndFlush(novoUsuario("del@email.com"));
        assertEquals(antes + 1, repository.count());

        repository.deleteById(salvo.getId());
        repository.flush();

        assertFalse(repository.existsById(salvo.getId()));
        assertEquals(antes, repository.count());
    }
}