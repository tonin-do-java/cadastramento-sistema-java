package com.sistemadecadastramento.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemadecadastramento.dtos.UsuarioCreateRequestDto;
import com.sistemadecadastramento.dtos.UsuarioRequestDto;
import com.sistemadecadastramento.dtos.UsuarioResponseDto;
import com.sistemadecadastramento.exceptions.CamposIncorretosException;
import com.sistemadecadastramento.exceptions.UsuarioJaCadastradoException;
import com.sistemadecadastramento.exceptions.UsuarioNaoCadastradoException;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
   
    private final UsuarioRepository repository;


    public List<UsuarioResponseDto> listarTodos(){
        List<Usuario> usuarios = repository.findAll();
        return usuarios.stream().map(usuario -> new UsuarioResponseDto(usuario)).toList();
    }

    public UsuarioResponseDto buscarPorId(Long id){
        Usuario usuario = repository.findById(id)
        .orElseThrow(() -> new UsuarioNaoCadastradoException());

        return new UsuarioResponseDto(usuario);
    }

    public Usuario buscarPorEmail(String email){
        Usuario usuario = repository.findByEmail(email)
        .orElseThrow(() -> new UsuarioNaoCadastradoException());

        return usuario;
    }

    public Usuario salvarCriar(Usuario usuario){
        if(repository.existsByEmail(usuario.getEmail())){
            throw new UsuarioJaCadastradoException();
        }
        
        return repository.save(usuario);
    }

    public UsuarioResponseDto salvarAtualizar(Long id, UsuarioRequestDto dto){
        
        Usuario usuarioExistente = repository.findById(id).orElseThrow(() -> new UsuarioNaoCadastradoException());

        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setEmail(dto.getEmail());

        Usuario usuarioAtual = repository.save(usuarioExistente);

        return new UsuarioResponseDto(usuarioAtual);
    }

    public void deletar(Long id){
        if(!repository.existsById(id)){
            throw new UsuarioNaoCadastradoException();
        }
        repository.deleteById(id);
    }

    public Usuario transformarDto(UsuarioCreateRequestDto requestDto){
        Usuario dadosUsuario = new Usuario();
        dadosUsuario.setNome(requestDto.getNome());
        dadosUsuario.setEmail(requestDto.getEmail());
        if(!requestDto.getSenha().equals(requestDto.getConfirmacaoSenha())){
            throw new CamposIncorretosException();
        }
        dadosUsuario.setSenhaHash(requestDto.getSenha());
        return dadosUsuario;
    }
}
