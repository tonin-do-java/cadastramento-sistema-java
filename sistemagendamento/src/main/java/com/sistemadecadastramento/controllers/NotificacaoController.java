package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemadecadastramento.dtos.NotificacaoRequestDto;
import com.sistemadecadastramento.dtos.NotificacaoResponseDto;
import com.sistemadecadastramento.services.NotificacaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificacaoController {
    
    private NotificacaoService service;

    @GetMapping("/notificacoes")
    public ResponseEntity<List<NotificacaoResponseDto>> listarTodos(){
        List<NotificacaoResponseDto> notificacoes = service.listarNotificacao();

        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/notificacoes/nao-lida")
    public ResponseEntity<List<NotificacaoResponseDto>> listarNaoLidas(){
        List<NotificacaoResponseDto> notificacoes = service.listarNotNaoLidas();

        return ResponseEntity.ok(notificacoes);
    }

    @PatchMapping("/notificacoes/{id}/ler")
    public ResponseEntity<NotificacaoResponseDto> lerNotificacao(@PathVariable Long id){
        NotificacaoResponseDto notificacao = service.lerMensagem(id);

        return ResponseEntity.ok(notificacao);
    }

    @PatchMapping("/notificacoes/ler-todas")
    public ResponseEntity<Void> lerTodasNotificacoes(){
        service.lerTodasMensagens();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notificacoes")
    public ResponseEntity<NotificacaoResponseDto> criarNotificacao(@RequestBody @Valid NotificacaoRequestDto dto){
        NotificacaoResponseDto resposta = service.salvarCriar(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();
        
        return ResponseEntity.created(uri).body(resposta);
    }
}
