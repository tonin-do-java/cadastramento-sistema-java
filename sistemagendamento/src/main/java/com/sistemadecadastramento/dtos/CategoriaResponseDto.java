package com.sistemadecadastramento.dtos;

import com.sistemadecadastramento.models.Categoria;

import lombok.Getter;

@Getter
public class CategoriaResponseDto {
    private Long id;
    private String nome;
    private String descricao;
    private boolean ativo;

    public CategoriaResponseDto(Categoria categoriaEntity){
        this.id = categoriaEntity.getId();
        this.nome = categoriaEntity.getNome();
        this.descricao = categoriaEntity.getDescricao();
        this.ativo = categoriaEntity.isAtivo();
    }
}
