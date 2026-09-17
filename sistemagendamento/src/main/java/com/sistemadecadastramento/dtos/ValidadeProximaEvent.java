package com.sistemadecadastramento.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.sistemadecadastramento.models.Produto;

@Data
@AllArgsConstructor
public class ValidadeProximaEvent {
    private Produto produto;
    private Long diasRestantes;
    private Long MovimentacaoId;
}
