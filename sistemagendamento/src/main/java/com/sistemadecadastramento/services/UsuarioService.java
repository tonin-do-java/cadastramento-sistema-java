package com.sistemadecadastramento.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

    public Usuario salvar(Usuario usuario){
        return repository.save(usuario);
    }

    public boolean verificarUsuarioPorId(Long id){
        // necessario implementar
        return repository.existsById(id);
    }

    public void deletar(Long id){
        // necessario implementar
        repository.deleteById(id);
    }
}
