package com.sistemadecadastramento.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sistemadecadastramento.dtos.UsuarioCreateRequestDto;
import com.sistemadecadastramento.dtos.UsuarioResponseDto;
import com.sistemadecadastramento.exceptions.CamposIncorretosException;
import com.sistemadecadastramento.exceptions.UsuarioJaCadastradoException;
import com.sistemadecadastramento.exceptions.UsuarioNaoCadastradoException;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository repository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UsuarioService service;
    
    @Test
    @DisplayName("deve definir o role ADMIN para o primerio usuario cadastrado")
    void deveDefinirRoleAdminParaPrimeiroUsuario(){
        UsuarioCreateRequestDto dto = new UsuarioCreateRequestDto();
    
        dto.setNome("Primeiro admin");
        dto.setEmail("teste@gmail.com");
        dto.setSenha("13345678");
        dto.setConfirmacaoSenha("13345678");
    
        when(repository.count()).thenReturn(0L);
        when(passwordEncoder.encode("133456")).thenReturn("senha_criptografada");
    
        Usuario resultado = service.transformarDto(dto);
    
        assertEquals(Roles.ADMIN, resultado.getRole());
        assertEquals("senha_criptografada", resultado.getSenhaHash());
    }

    @Test
    @DisplayName("este deve definir a Role para USER se tiver mais de um usuario")
    void deveDefinirRoleUserParaRestante(){
        UsuarioCreateRequestDto dto = new UsuarioCreateRequestDto();
    
        dto.setNome("usuario normal");
        dto.setEmail("teste@gmail.com");
        dto.setSenha("13345678");
        dto.setConfirmacaoSenha("13345678");
    
        when(repository.count()).thenReturn(1L);
        when(passwordEncoder.encode("13345678")).thenReturn("senha_criptografada");
    
        Usuario resultado = service.transformarDto(dto);
    
        assertEquals(Roles.USER, resultado.getRole());
        assertEquals("senha_criptografada", resultado.getSenhaHash());
    }

    @Test
    @DisplayName("deve retornar uma Exceção se as senhas não estiverem certas")
    void deveLançarExcecaoSenhaErrada(){
        UsuarioCreateRequestDto dto = new UsuarioCreateRequestDto();
    
        dto.setNome("usuario normal");
        dto.setEmail("teste@gmail.com");
        dto.setSenha("13345678");
        dto.setConfirmacaoSenha("567890123");

        

        CamposIncorretosException excecao = assertThrows(CamposIncorretosException.class,
            () -> {
                if(dto.getSenha() != dto.getConfirmacaoSenha()){
                    throw new CamposIncorretosException();
                }
            }
        );

        assertEquals("Os campos estão incorretos!", excecao.getMessage());

    }

    @Test
    @DisplayName("deve verificar se a algum email igual")
    void deveVerificarDuplicidadeEmail(){
        UsuarioCreateRequestDto dto1 = new UsuarioCreateRequestDto();
    
        dto1.setNome("usuario 1");
        dto1.setEmail("teste@gmail.com");
        dto1.setSenha("13345678");
        dto1.setConfirmacaoSenha("13345678");

        UsuarioCreateRequestDto dto = new UsuarioCreateRequestDto();

        String email = "teste@gmail.com";
        dto.setNome("usuario 2");
        dto.setEmail(email);
        dto.setSenha("13345678");
        dto.setConfirmacaoSenha("13345678");

        UsuarioJaCadastradoException excecao = assertThrows(UsuarioJaCadastradoException.class, 
            () -> {
                if(dto.getEmail() == dto1.getEmail()){
                    throw new UsuarioJaCadastradoException();
                }
            }
        );

        assertEquals("O usuario que usa esse email, já está cadastrado!", excecao.getMessage());

        
    }
    
    @Test
    @DisplayName("Deve salvar o usuário mantendo a Role ADMIN se ela já tiver sido definida")
    void deveSalvarUsuarioMantendoRoleAdminSeJaDefinida() {
        
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setNome("Admin Existente");
        usuarioAdmin.setEmail("admin@teste.com");
        usuarioAdmin.setRole(Roles.ADMIN); 

        when(repository.existsByEmail("admin@teste.com")).thenReturn(false);
        
        when(repository.save(usuarioAdmin)).thenReturn(usuarioAdmin);

        Usuario usuarioSalvo = service.salvarCriar(usuarioAdmin);

        assertEquals(Roles.ADMIN, usuarioSalvo.getRole());
    }

    @Test
    @DisplayName("Deve retornar UsuarioResponseDto quando o ID do usuário existir no banco")
    void deveRetornarUsuarioResponseDtoQuandoIdExistir() {

        Long idExistente = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(idExistente);
        usuarioMock.setNome("Natan");
        usuarioMock.setEmail("natan@email.com");

        when(repository.findById(idExistente)).thenReturn(java.util.Optional.of(usuarioMock));

        UsuarioResponseDto resultado = service.buscarPorId(idExistente);

        assertEquals("Natan", resultado.getNome());
        assertEquals("natan@email.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoCadastradoException quando o ID do usuário não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {

        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(java.util.Optional.empty());

        
        org.junit.jupiter.api.Assertions.assertThrows(
            UsuarioNaoCadastradoException.class, 
            () -> service.buscarPorId(idInexistente)
        );
    }

    @Test
    @DisplayName("Deve retornar UsuarioResponseDto quando o Email do usuário existir no banco")
    void deveRetornarUsuarioResponseDtoQuandoEmailExistir() {

        Usuario usuarioMock = new Usuario();
        usuarioMock.setNome("Natan");
        String us = "natan@email.com";
        usuarioMock.setEmail(us);
        when(repository.findByEmail(us)).thenReturn(java.util.Optional.of(usuarioMock));

        Usuario resultado = service.buscarPorEmail(us);

        assertEquals("Natan", resultado.getNome());
        assertEquals("natan@email.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoCadastradoException quando o Email do usuário não existir")
    void deveLancarExcecaoQuandoEmailNaoExistir() {

        String email = "nadahaver@gmail.com";

        when(repository.findByEmail(email)).thenReturn(java.util.Optional.empty());

        
        org.junit.jupiter.api.Assertions.assertThrows(
            UsuarioNaoCadastradoException.class, 
            () -> service.buscarPorEmail(email)
        );
    }

    @Test
    @DisplayName("Deve deletar Usuario quando o ID do usuário existir no banco")
    void deveDeletarUsuarioQuandoIdExistir() {

        Long idExistente = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(idExistente);
        usuarioMock.setNome("Natan");
        usuarioMock.setEmail("natan@email.com");

        when(repository.existsById(idExistente)).thenReturn(true);

        service.deletar(idExistente);

        org.mockito.Mockito.verify(repository, org.mockito.Mockito.times(1)).deleteById(idExistente);
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoCadastradoException e não deletar se o ID não existir")
    void deveLancarExcecaoETentarNaoDeletarQuandoIdNaoExistir() {
        Long idInexistente = 999L;

        when(repository.existsById(idInexistente)).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(
            UsuarioNaoCadastradoException.class,
            () -> service.deletar(idInexistente)
        );

        org.mockito.Mockito.verify(repository, org.mockito.Mockito.never()).deleteById(idInexistente);
    }
}
