package com.sistemadecadastramento.dtos;

import java.math.BigDecimal;

import com.sistemadecadastramento.models.Produto;

import lombok.Getter;

@Getter
public class ProdutoResponseDto {
    private Long id;
    private Long categoriaId;
    private String nomeUsuario;
    private String codigo;
    private String nome;
    private String descricao;
    private String marca;
    private String unidadeMedida;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private BigDecimal margemLucro;
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private Integer quantidadeAtual;
    private Boolean controlaLote;
    private Boolean controlaValidade;
    private Boolean ativo;
    
    public ProdutoResponseDto(Produto produtoEntity) {
        this.id = produtoEntity.getId();
        this.categoriaId = produtoEntity.getCategoria().getId();
        this.nomeUsuario = produtoEntity.getUsuario().getNome();
        this.codigo = produtoEntity.getCodigo();
        this.nome = produtoEntity.getNome();
        this.descricao = produtoEntity.getDescricao();
        this.marca = produtoEntity.getMarca();
        this.unidadeMedida = produtoEntity.getUnidadeMedida();
        this.precoCusto = produtoEntity.getPrecoCusto();
        this.precoVenda = produtoEntity.getPrecoVenda();
        this.margemLucro = produtoEntity.getMargemLucro();
        this.estoqueMinimo = produtoEntity.getEstoqueMinimo();
        this.estoqueMaximo = produtoEntity.getEstoqueMaximo();
        this.quantidadeAtual = produtoEntity.getQuantidadeAtual();
        this.controlaLote = produtoEntity.getControlaLote();
        this.controlaValidade = produtoEntity.getControlaValidade();
        this.ativo = produtoEntity.getAtivo();
    }

    
}
