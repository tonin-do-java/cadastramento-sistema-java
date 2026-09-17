package com.sistemadecadastramento.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.sistemadecadastramento.models.Produto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueBaixoEvent {
    private Produto produto;
    private Long MovimentacaoId;
}
