package com.sistemadecadastramento.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sistemadecadastramento.infra.UsuarioJaCadastradoException;
import com.sistemadecadastramento.infra.UsuarioNaoExisteException;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
   
    private final UsuarioRepository repository;


    public List<Usuario> listarTodos(){
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id){
        return repository.findById(id);
    }

    public Usuario salvarCriar(Usuario usuario){
        if(repository.existsById(usuario.getId())){
            throw new UsuarioJaCadastradoException();
        }
        return repository.save(usuario);
    }

    public Usuario salvarAtualizar(Usuario usuario){
        if(!existePorId(usuario.getId())){
            throw new UsuarioNaoExisteException();
        }

        return repository.save(usuario);
    }

    public boolean existePorId(Long id){
        return repository.existsById(id);
    }

    public void deletar(Long id){
        if(!repository.existsById(id)){
            throw new UsuarioNaoExisteException();
        }
        repository.deleteById(id);
    }
}
