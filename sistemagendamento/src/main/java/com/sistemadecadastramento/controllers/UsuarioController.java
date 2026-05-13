package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemadecadastramento.infra.UsuarioNaoExisteException;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.UsuarioService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {
    
    private final UsuarioService service;

    @GetMapping("/listarTodos")
    public ResponseEntity<List<Usuario>> listarTodos(){
        List<Usuario> usuarios = service.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/listarPorId/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id){
        Optional<Usuario> usuarioEncontrado = service.buscarPorId(id);
        return usuarioEncontrado
                .map(usuarioExis -> ResponseEntity.ok(usuarioExis))
                .orElseThrow(() -> new UsuarioNaoExisteException());
    }

    @PostMapping("/Cadastrar")
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario){
        Usuario usuarioSalvo = service.salvarCriar(usuario);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioSalvo.getId())
                .toUri();
        
        return ResponseEntity.created(uri).body(usuarioSalvo);
    }

    @PutMapping("/AtualizarCadastro/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado){

        usuarioAtualizado.setId(id); 
        Usuario usuarioAtual = service.salvarAtualizar(usuarioAtualizado);

        return ResponseEntity.ok(usuarioAtual);
    }
    
    @DeleteMapping("/deletarUsuario")
    public ResponseEntity<Usuario> deletarUsuario(Long id){
        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
