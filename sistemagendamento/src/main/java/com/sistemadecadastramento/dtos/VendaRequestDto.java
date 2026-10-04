package com.sistemadecadastramento.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.sistemadecadastramento.models.FormaPagamento;
import com.sistemadecadastramento.models.Status;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class VendaRequestDto {
    @NotNull(message = "o cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "vendedor é obrigatório")
    private Long vendedorId;

    @NotNull(message = "o status é obrigatório")
    private Status status;

    @NotNull(message = "a forma de pagamento é obrigatória")
    private FormaPagamento formaPagamento;

    @NotNull(message = "tem que haver itens de venda")
    private List<ItemVendaRequestDto> itens;

    private BigDecimal desconto;
    private String observacao;
    
}
