package com.sistemadecadastramento.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

@DisplayName("Usuario (model) - testes unitários")
class UsuarioTest {

    private List<String> authorities(Usuario u) {
        return u.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("ADMIN deve ter a authority ROLE_ADMIN")
    void getAuthorities_Admin() {
        Usuario u = new Usuario();
        u.setRole(Roles.ADMIN);

        assertEquals(List.of("ROLE_ADMIN"), authorities(u));
    }

    @Test
    @DisplayName("ESTOQUISTA deve ter a authority ROLE_ESTOQUISTA")
    void getAuthorities_Estoquista() {
        Usuario u = new Usuario();
        u.setRole(Roles.ESTOQUISTA);

        assertEquals(List.of("ROLE_ESTOQUISTA"), authorities(u));
    }

    @Test
    @DisplayName("USER deve ter a authority ROLE_USER")
    void getAuthorities_User() {
        Usuario u = new Usuario();
        u.setRole(Roles.USER);

        assertEquals(List.of("ROLE_USER"), authorities(u));
    }

    @Test
    @DisplayName("getUsername deve retornar o e-mail e getPassword o hash da senha")
    void getUsernameEGetPassword() {
        Usuario u = new Usuario();
        u.setEmail("natan@email.com");
        u.setSenhaHash("hash-bcrypt");

        assertEquals("natan@email.com", u.getUsername());
        assertEquals("hash-bcrypt", u.getPassword());
    }

    @Test
    @DisplayName("A conta deve estar sempre ativa, não expirada e não bloqueada")
    void flagsDeContaDevemSerTrue() {
        Usuario u = new Usuario();

        assertTrue(u.isAccountNonExpired());
        assertTrue(u.isAccountNonLocked());
        assertTrue(u.isCredentialsNonExpired());
        assertTrue(u.isEnabled());
    }

    @Test
    @DisplayName("Construtor completo deve preencher todos os campos")
    void construtorCompleto() {
        Usuario u = new Usuario(1L, "Natan", "natan@email.com", "hash", Roles.ADMIN);

        assertEquals(1L, u.getId());
        assertEquals("Natan", u.getNome());
        assertEquals("natan@email.com", u.getEmail());
        assertEquals("hash", u.getSenhaHash());
        assertEquals(Roles.ADMIN, u.getRole());
    }

    @Test
    @DisplayName("equals/hashCode (Lombok @Data) devem considerar os campos")
    void equalsEHashCode() {
        Usuario a = new Usuario(1L, "Natan", "natan@email.com", "hash", Roles.USER);
        Usuario b = new Usuario(1L, "Natan", "natan@email.com", "hash", Roles.USER);
        Usuario c = new Usuario(2L, "Outro", "outro@email.com", "hash", Roles.USER);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}