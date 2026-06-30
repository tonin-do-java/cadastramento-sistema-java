package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemadecadastramento.dtos.MovimentacaoRequestDto;
import com.sistemadecadastramento.dtos.MovimentacaoResponseDto;
import com.sistemadecadastramento.services.MovimentacaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovimentacoesController {

    private final MovimentacaoService service;

    @GetMapping("/movimentacao")
    public ResponseEntity<List<MovimentacaoResponseDto>> listarTodos(){
        List<MovimentacaoResponseDto> movimentacoes = service.listarHistorico();

        return ResponseEntity.ok(movimentacoes);
    }

    @GetMapping("/movimentacao/produto/{produtoId}")
    public ResponseEntity<List<MovimentacaoResponseDto>> listarPorProduto(@PathVariable Long produtoId){
        List<MovimentacaoResponseDto> movimentacoes = service.listarHistoricoPorProduto(produtoId);

        return ResponseEntity.ok(movimentacoes);
    }

    @PostMapping("/movimentacao")
    public ResponseEntity<MovimentacaoResponseDto> registrarHistorico(MovimentacaoRequestDto dto){
        MovimentacaoResponseDto resposta = service.registrarMovimentacao(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();

        return ResponseEntity.created(uri).body(resposta);
    }
}
