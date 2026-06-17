package com.sistemadecadastramento.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaRequestDto{
    
    @NotBlank(message = "O nome da Categoria não pode ficar em branco")
    private String nome;
    
    @NotBlank(message = "Coloque uma descrição sobre esta categoria")
    private String descricao;
}