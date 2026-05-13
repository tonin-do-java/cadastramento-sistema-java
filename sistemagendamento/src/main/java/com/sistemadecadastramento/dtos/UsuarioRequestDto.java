package com.sistemadecadastramento.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDto {
    @NotBlank(message = "O nome do usuario não pode ser vazio")
    private String nome;

    @Email(message = "Formato de Email inválido")
    @NotBlank(message = "O Email não pode estar em branco")
    private String email;

    @Size(min = 6, message = "coloque no minimo 6 caracteres")
    @NotBlank(message = "A senha não pode estar vazia")
    private String senha;

    @Size(min = 6, message = "coloque no minimo 6 caracteres")
    @NotBlank(message = "A confirmação da senha não pode estar vazia")
    private String confirmacaoSenha;
}
