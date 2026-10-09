package com.sistemadecadastramento.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemadecadastramento.dtos.VendaRequestDto;
import com.sistemadecadastramento.dtos.VendaResponseDto;
import com.sistemadecadastramento.models.Status;
import com.sistemadecadastramento.services.VendaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VendaController {
    
    private final VendaService service;

    @GetMapping("/vendas")
    public ResponseEntity<List<VendaResponseDto>> listarTodos(@RequestParam(required = false) Status status, @RequestParam(required = false) LocalDateTime dataHora, @RequestParam(required = false) Long clienteId){
        List<VendaResponseDto> vendas = service.listarComFiltros(status, dataHora, clienteId);

        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/vendas/{id}")
    public ResponseEntity<VendaResponseDto> buscarPorId(@PathVariable Long id){
        VendaResponseDto dto = new VendaResponseDto(service.buscarPorId(id));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/vendas")
    public ResponseEntity<VendaResponseDto> criarVenda(@RequestBody @Valid VendaRequestDto dto){
        VendaResponseDto resposta = service.salvarCriar(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.getId())
                .toUri();

        return ResponseEntity.created(uri).body(resposta);
    }

    @PatchMapping("/vendas/{id}/status")
    public ResponseEntity<VendaResponseDto> alterarStatus(@PathVariable Long id, @RequestParam("status") Status status){
        VendaResponseDto novoStatus = service.alterarStatus(id, status);

        return ResponseEntity.ok(novoStatus);
    }
}
