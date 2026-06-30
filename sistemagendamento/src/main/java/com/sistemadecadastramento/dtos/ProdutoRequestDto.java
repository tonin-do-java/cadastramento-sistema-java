package com.sistemadecadastramento.dtos;

import java.math.BigDecimal;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoRequestDto {
    @NotNull(message = "Tem que selecionar uma categoria")
    private Long categoriaId;

    @NotBlank(message = "O código do produto não pode ficar vazio")
    private String codigo;

    @NotBlank(message = "O nome do produto não pode ficar vazio")
    private String nome;

    @NotBlank(message = "A descrição não pode ficar vazia")
    private String descricao;

    @NotBlank(message = "A marca não pode ficar vazia")
    private String marca;

    @NotBlank(message = "Coloque uma unidade de medida")
    private String unidadeMedida;

    @NotNull(message = "O preço de custo não pode ficar vazio")
    @Positive(message = "O valor tem que ser maior que 0")
    private BigDecimal precoCusto;

    @NotNull(message = "O preco de venda não pode ficar vazio")
    @Positive(message = "O valor tem que ser maior que 0")
    private BigDecimal precoVenda;

    @NotNull(message = "O estoque minimo não pode ficar em branco")
    @Positive(message = "O estoque minimo tem que ser maior do que 0")
    private Integer estoqueMinimo;

    @NotNull(message = "O estoque máximo não pode ficar em branco")
    @Positive(message = "O estoque máximo tem que ser maior do que 0")
    private Integer estoqueMaximo;

    private Boolean controlaLote = true;
    private Boolean controlaValidade = true;
}
