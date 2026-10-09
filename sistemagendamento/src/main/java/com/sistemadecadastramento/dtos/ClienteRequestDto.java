package com.sistemadecadastramento.dtos;

import com.sistemadecadastramento.infra.config.CpfOuCnpjValido;
import com.sistemadecadastramento.models.TipoPessoa;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CpfOuCnpjValido 
public class ClienteRequestDto {
    @NotBlank(message = "o nome é obrigatório")
    private String nome;

    @NotNull(message = "o tipo de pessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @NotBlank(message = "o documento é obrigatório")
    private String documento;


    private String nomeFantasia;

    @Pattern(regexp = "^(10|11|2[0-9])\\.\\d{3}\\.\\d{3}-\\d{1}$", message = "escreva corretamente os numeros")
    private String inscricaoEstadual;

    @Valid
    private EnderecoDto endereco;

    @Valid
    private ContatoDto contato;

}
