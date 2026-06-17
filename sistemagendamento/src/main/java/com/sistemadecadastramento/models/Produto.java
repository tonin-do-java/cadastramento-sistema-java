package com.sistemadecadastramento.models;

import java.math.BigDecimal;

public class Produto {
    private Long id;
    private String codigo;
    private String codigoBarras;
    private String nome;
    private String descricao;
    private String marca;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private BigDecimal margemLucro;
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private Boolean controlaLote;
    private Boolean controlaValidade;
    private Boolean ativo;
}
