package com.sistemadecadastramento.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "codigo_barras", nullable = false)
    private String codigoBarras;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descrição", nullable = false)
    private String descricao;

    @Column(name = "marca", nullable = false)
    private String marca;

    @Column(name = "unidade_medida", nullable = false)
    private String UnidadeMedida;

    @Column(name = "preco_custo", nullable = false)
    private BigDecimal precoCusto;

    @Column(name = "preço_venda", nullable = false)
    private BigDecimal precoVenda;

    @Column(name = "margem_lucro", nullable = false)
    private BigDecimal margemLucro;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    @Column(name = "estoque_máximo", nullable = false)
    private Integer estoqueMaximo;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual = 0;

    @Column(name = "controla_lote", nullable = false)
    private Boolean controlaLote;

    @Column(name = "controla_validade", nullable = false)
    private Boolean controlaValidade;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;
}
