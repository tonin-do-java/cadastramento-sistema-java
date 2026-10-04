package com.sistemadecadastramento.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class ItemVendaRequestDto {
    @NotNull(message = "venda é obrigatório")
    private Long vendaId;

    @NotNull(message = "produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "quantidade é obrigatório")
    @Positive(message = "quantidade tem que ser maior que zero")
    private Integer quantidade;

    @PositiveOrZero(message = "o valor tem que ser 0 ou valores acima de 0")
    private BigDecimal desconto;

}
