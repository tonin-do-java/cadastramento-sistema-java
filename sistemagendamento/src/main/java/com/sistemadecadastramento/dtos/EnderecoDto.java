package com.sistemadecadastramento.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDto {

    @NotBlank(message = "é obrigatório ter CEP")
    @Pattern(regexp = "\\d{5} - d{3}")
    private String cep;

    @NotBlank(message = "é obrigatório ter um logradouro")
    private String logradouro;

    @Pattern(regexp = "^[0-9]+$", message = "o campo pode receber somente número")
    private String numero;
    
    @NotBlank(message = "é obrigatório ter um bairro")
    private String bairro;
    
    @NotBlank(message = "é obrigatório ter uma cidade")
    private String cidade;
    
    @NotBlank(message = "é obrigatório ter um estado")
    private String estado;
}
