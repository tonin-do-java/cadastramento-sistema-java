package com.sistemadecadastramento.dtos;

import java.time.LocalDate;

import com.sistemadecadastramento.models.OrigemMovimentacao;
import com.sistemadecadastramento.models.TipoMovimentacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoRequestDto {
    @NotNull(message = "O produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @NotNull(message = "O tipo de movimentação (ENTRADA/SAIDA) é obrigatório")
    private TipoMovimentacao tipo;

    @NotNull(message = "A origem da movimentação é obrigatório")
    private OrigemMovimentacao origem;

    private String lote;

    private LocalDate dataValidade;
}
