package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemadecadastramento.dtos.ClienteResponseDto;
import com.sistemadecadastramento.dtos.ClienteRequestDto;
import com.sistemadecadastramento.models.TipoPessoa;
import com.sistemadecadastramento.services.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClienteController {
    
    private final ClienteService service;

    @GetMapping("/cliente")
    public ResponseEntity<List<ClienteResponseDto>> listarTodos(@RequestParam(required = false) TipoPessoa tipoPessoa, @RequestParam(required = false) String cidade){
        List<ClienteResponseDto> clientes = service.listarComFiltros(tipoPessoa, cidade);

        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<ClienteResponseDto> buscarPorId(@PathVariable Long id){
        ClienteResponseDto dto = new ClienteResponseDto(service.buscarPorId(id));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/cliente")
    public ResponseEntity<ClienteResponseDto> criarCliente(@Valid @RequestBody ClienteRequestDto dto){
        ClienteResponseDto resposta = service.salvarCriar(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();

        return ResponseEntity.created(uri).body(resposta);
    }

    @PutMapping("/cliente/{id}")
    public ResponseEntity<ClienteResponseDto> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequestDto dto){
        ClienteResponseDto clienteAtualizado = service.salvarAtualizar(id, dto);

        return ResponseEntity.ok(clienteAtualizado);
    }

    @PatchMapping("/cliente/{id}")
    public ResponseEntity<ClienteResponseDto> alterarAtividade(@PathVariable Long id){
        ClienteResponseDto atividade = service.alterarAtividade(id);

        return ResponseEntity.ok(atividade);
    }
}
