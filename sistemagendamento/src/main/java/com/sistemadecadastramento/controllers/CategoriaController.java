package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemadecadastramento.dtos.CategoriaRequestDto;
import com.sistemadecadastramento.dtos.CategoriaResponseDto;
import com.sistemadecadastramento.services.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoriaController {
    
    private final CategoriaService service;

    @GetMapping("/categoria")
    public ResponseEntity<List<CategoriaResponseDto>> listarTodos(){
        List<CategoriaResponseDto> categorias = service.listarTodos();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categoria/{id}")
    public ResponseEntity<CategoriaResponseDto> buscarPorId(@PathVariable Long id){
        CategoriaResponseDto dto = service.buscarPorId(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/categoria")
    public ResponseEntity<CategoriaResponseDto> criarCategoria(@Valid @RequestBody CategoriaRequestDto requestDto){
        CategoriaResponseDto resposta = service.salvarCriar(requestDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();
        
        return ResponseEntity.created(uri).body(resposta);
    }

    @PutMapping("/categoria/{id}")
    public ResponseEntity<CategoriaResponseDto> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDto dto){
        CategoriaResponseDto categoriaAtualizada = service.salvarAtualizar(id, dto);

        return ResponseEntity.ok(categoriaAtualizada);
    }

    @PutMapping("/categoria/{id}/atividade")
    public ResponseEntity<CategoriaResponseDto> alterarAtividade(@PathVariable Long id){

        service.alterarAtividade(id);

        return ResponseEntity.noContent().build();
    }
}
