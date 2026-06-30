package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemadecadastramento.dtos.ProdutoRequestDto;
import com.sistemadecadastramento.dtos.ProdutoResponseDto;
import com.sistemadecadastramento.services.ProdutoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProdutoController {
    
    private final ProdutoService service;

    @GetMapping("/produto")
    public ResponseEntity<List<ProdutoResponseDto>> listarPorFiltro(@RequestParam(required = false) String nome, @RequestParam(required = false) String codigo, @RequestParam(required = false) Long categoriaId){
        List<ProdutoResponseDto> produtos = service.listarComFiltros(nome, codigo, categoriaId);

        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/produto/{id}")
    public ResponseEntity<ProdutoResponseDto> buscarPorId(@PathVariable Long id){
        ProdutoResponseDto dto = service.buscarPorId(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/produto")
    public ResponseEntity<ProdutoResponseDto> criarProduto(@Valid @RequestBody ProdutoRequestDto requestDto){
        ProdutoResponseDto resposta = service.salvarCriar(requestDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();
        
        return ResponseEntity.created(uri).body(resposta);
    }

    @PutMapping("/produto/{id}")
    public ResponseEntity<ProdutoResponseDto> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDto dto){
        ProdutoResponseDto produtoAtualizado = service.salvarAtualizar(id, dto);

        return ResponseEntity.ok(produtoAtualizado);
    }

    @PutMapping("/produto/{id}/atividade")
    public ResponseEntity<ProdutoResponseDto> alterarAtividade(@PathVariable Long id){
        ProdutoResponseDto atividade = service.alterarAtividade(id);

        return ResponseEntity.ok(atividade);
    }
}
