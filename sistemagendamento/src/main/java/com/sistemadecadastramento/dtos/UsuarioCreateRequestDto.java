package com.sistemadecadastramento.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCreateRequestDto extends UsuarioRequestDto{
    @NotBlank(message = "A senha não pode estar vazia")
    @Size(min = 6, message = "coloque no minimo 6 caracteres")
    private String senha;

    @NotBlank(message = "A confirmação da senha não pode estar vazia")
    @Size(min = 6, message = "coloque no minimo 6 caracteres")
    private String confirmacaoSenha;
}
