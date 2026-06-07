package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.util.List;

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

import com.sistemadecadastramento.dtos.UsuarioCreateRequestDto;
import com.sistemadecadastramento.dtos.UsuarioRequestDto;
import com.sistemadecadastramento.dtos.UsuarioResponseDto;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UsuarioController {
    
    private final UsuarioService service;

    @GetMapping("/usuarios/listarTodos")
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos(){
        List<UsuarioResponseDto> usuarios = service.listarTodos();
        
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/listarPorId/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable Long id){
        UsuarioResponseDto dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/usuarios/Cadastrar")
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@Valid @RequestBody UsuarioCreateRequestDto requestDto){
        
        Usuario usuarioSalvo = service.salvarCriar(service.transformarDto(requestDto));
        UsuarioResponseDto resposta = new UsuarioResponseDto(usuarioSalvo);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();
        
        return ResponseEntity.created(uri).body(resposta);
    }

    @PutMapping("/usuarios/AtualizarCadastro/{id}")
    public ResponseEntity<UsuarioResponseDto> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDto dto){

        UsuarioResponseDto usuarioAtual = service.salvarAtualizar(id, dto);

        return ResponseEntity.ok(usuarioAtual);
    }
    
    @DeleteMapping("/usuarios/deletarUsuario/{id}")
    public ResponseEntity<Usuario> deletarUsuario(@PathVariable Long id){
        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
