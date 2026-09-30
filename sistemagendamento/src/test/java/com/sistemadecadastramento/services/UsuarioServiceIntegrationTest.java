package com.sistemadecadastramento.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sistemadecadastramento.dtos.UsuarioCreateRequestDto;
import com.sistemadecadastramento.dtos.UsuarioRequestDto;
import com.sistemadecadastramento.dtos.UsuarioResponseDto;
import com.sistemadecadastramento.exceptions.UsuarioJaCadastradoException;
import com.sistemadecadastramento.exceptions.UsuarioNaoCadastradoException;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.UsuarioRepository;


@DataJpaTest
@Import({ UsuarioService.class, UsuarioServiceIntegrationTest.Config.class })
@DisplayName("UsuarioService - integração com banco e BCrypt")
class UsuarioServiceIntegrationTest {

    @TestConfiguration
    static class Config {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UsuarioCreateRequestDto dto(String email, Roles role) {
        UsuarioCreateRequestDto d = new UsuarioCreateRequestDto();
        d.setNome("Usuario " + email);
        d.setEmail(email);
        d.setSenha("senha1234");
        d.setConfirmacaoSenha("senha1234");
        d.setRole(role);
        return d;
    }

    @Test
    @DisplayName("Primeiro usuário cadastrado deve ser ADMIN e ter a senha criptografada no banco")
    void primeiroUsuario_DeveSerAdminComSenhaHash() {
        Usuario salvo = service.salvarCriar(service.transformarDto(dto("primeiro@email.com", Roles.USER)));

        Usuario doBanco = repository.findById(salvo.getId()).orElseThrow();
        assertEquals(Roles.ADMIN, doBanco.getRole());
        assertNotEquals("senha1234", doBanco.getSenhaHash());
        assertTrue(passwordEncoder.matches("senha1234", doBanco.getSenhaHash()));
    }

    @Test
    @DisplayName("Segundo usuário deve manter o role informado")
    void segundoUsuario_DeveManterRole() {
        service.salvarCriar(service.transformarDto(dto("primeiro@email.com", Roles.USER)));

        Usuario segundo = service.salvarCriar(service.transformarDto(dto("segundo@email.com", Roles.ESTOQUISTA)));

        assertEquals(Roles.ESTOQUISTA, repository.findById(segundo.getId()).orElseThrow().getRole());
    }

    @Test
    @DisplayName("Não deve permitir dois usuários com o mesmo e-mail")
    void emailDuplicado_DeveLancarExcecao() {
        service.salvarCriar(service.transformarDto(dto("dup@email.com", Roles.USER)));

        Usuario duplicado = service.transformarDto(dto("dup@email.com", Roles.USER));

        assertThrows(UsuarioJaCadastradoException.class, () -> service.salvarCriar(duplicado));
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("salvarAtualizar deve persistir as alterações")
    void salvarAtualizar_DevePersistir() {
        Usuario salvo = service.salvarCriar(service.transformarDto(dto("antigo@email.com", Roles.USER)));

        UsuarioResponseDto resposta = service.salvarAtualizar(salvo.getId(),
                new UsuarioRequestDto("Nome Novo", "novo@email.com", Roles.ESTOQUISTA));

        Usuario doBanco = repository.findById(salvo.getId()).orElseThrow();
        assertEquals("Nome Novo", doBanco.getNome());
        assertEquals("novo@email.com", doBanco.getEmail());
        assertEquals(Roles.ESTOQUISTA, doBanco.getRole());
        assertEquals(salvo.getId(), resposta.getId());
    }

    @Test
    @DisplayName("buscarPorEmail e buscarPorId devem encontrar o usuário persistido")
    void buscas_DevemEncontrarUsuario() {
        Usuario salvo = service.salvarCriar(service.transformarDto(dto("busca@email.com", Roles.USER)));

        assertNotNull(service.buscarPorEmail("busca@email.com"));
        assertEquals("busca@email.com", service.buscarPorId(salvo.getId()).getEmail());
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    @DisplayName("deletar deve remover do banco e depois lançar exceção ao buscar")
    void deletar_DeveRemoverDoBanco() {
        Usuario salvo = service.salvarCriar(service.transformarDto(dto("del@email.com", Roles.USER)));

        service.deletar(salvo.getId());

        assertFalse(repository.existsById(salvo.getId()));
        assertThrows(UsuarioNaoCadastradoException.class, () -> service.buscarPorId(salvo.getId()));
        assertThrows(UsuarioNaoCadastradoException.class, () -> service.deletar(salvo.getId()));
    }
}