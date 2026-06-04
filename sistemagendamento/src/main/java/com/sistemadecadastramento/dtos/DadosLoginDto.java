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
public class DadosLoginDto {
    @NotBlank(message = "Esse Email não pode ficar em branco")
    @Email(message = "Formato de Email inválido")
    private String email;

    @NotBlank(message = "Senha não pode ficar em branco")
    @Size(min = 8, message = "Senha tem que ter pelo menos 8 caracteres")
    private String senha;
}
