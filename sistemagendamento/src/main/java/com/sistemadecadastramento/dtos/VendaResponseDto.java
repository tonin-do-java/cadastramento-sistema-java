package com.sistemadecadastramento.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.sistemadecadastramento.models.FormaPagamento;
import com.sistemadecadastramento.models.Status;
import com.sistemadecadastramento.models.Venda;

import lombok.Getter;

@Getter
public class VendaResponseDto {
    private Long id;
    private LocalDateTime dataHora;
    private Long clienteId;
    private Long vendedorId;
    private Status status;
    private FormaPagamento formaPagamento;
    private BigDecimal desconto;
    private BigDecimal valorTotal;
    private BigDecimal valorSubtotal;
    private String observacao;
    private List<ItemVendaResponseDto> itens;

    public VendaResponseDto(Venda vendaEntity){
        this.id = vendaEntity.getId();
        this.dataHora = vendaEntity.getDataHora();
        this.clienteId = vendaEntity.getCliente().getId();
        this.vendedorId = vendaEntity.getVendedor().getId();
        this.status = vendaEntity.getStatus();
        this.formaPagamento = vendaEntity.getFormaPagamento();
        this.desconto = vendaEntity.getDesconto();
        this.valorSubtotal = vendaEntity.getValorSubtotal();
        this.valorTotal = vendaEntity.getValorTotal();
        this.observacao = vendaEntity.getObservacao();
        this.itens = vendaEntity.getItens() != null 
                ? vendaEntity.getItens().stream().map(ItemVendaResponseDto::new).toList()
                : Collections.emptyList();

    }
}
