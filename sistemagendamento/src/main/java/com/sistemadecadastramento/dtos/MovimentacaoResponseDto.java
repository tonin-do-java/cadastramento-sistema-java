package com.sistemadecadastramento.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sistemadecadastramento.models.MovimentacaoEstoque;
import com.sistemadecadastramento.models.OrigemMovimentacao;
import com.sistemadecadastramento.models.TipoMovimentacao;

import lombok.Getter;

@Getter
public class MovimentacaoResponseDto {
    private Long id;
    private String produto;
    private String usuario;
    private TipoMovimentacao tipoMovimentacao;
    private OrigemMovimentacao origemMovimentacao;
    private Integer quantidade;
    private LocalDateTime dataHora;
    private LocalDate validade;
    private String lote;

    public MovimentacaoResponseDto(MovimentacaoEstoque movimentacaoEntity){
        this.id = movimentacaoEntity.getId();
        this.produto = movimentacaoEntity.getProduto().getNome();
        this.usuario = movimentacaoEntity.getUsuario().getNome();
        this.tipoMovimentacao = movimentacaoEntity.getTipoMovimentacao();
        this.origemMovimentacao = movimentacaoEntity.getOrigemMovimentacao();
        this.quantidade = movimentacaoEntity.getQuantidade();
        this.dataHora = movimentacaoEntity.getDataHora();
        this.validade = movimentacaoEntity.getValidade();
        this.lote = movimentacaoEntity.getLote();
    }
}
