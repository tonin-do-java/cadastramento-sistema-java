package com.sistemadecadastramento.dtos;

import java.math.BigDecimal;

import com.sistemadecadastramento.models.ItemVenda;

import lombok.Getter;

@Getter 
public class ItemVendaResponseDto {
    private Long id;
    private Long vendaId;
    private Long produtoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal desconto;
    private BigDecimal subtotal;

    public ItemVendaResponseDto(ItemVenda itemEntity){
        this.id = itemEntity.getId();
        this.vendaId = itemEntity.getVenda().getId();
        this.produtoId = itemEntity.getProduto().getId();
        this.quantidade = itemEntity.getQuantidade();
        this.precoUnitario = itemEntity.getPrecoUnitario();
        this.desconto = itemEntity.getDesconto();
        this.subtotal = itemEntity.getSubtotal();
    }
}
