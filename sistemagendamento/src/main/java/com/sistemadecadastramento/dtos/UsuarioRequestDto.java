package com.sistemadecadastramento.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDto {
    @NotBlank(message = "O nome do usuario não pode estar em branco")
    private String nome;

    @NotBlank(message = "O Email não pode estar em branco")
    @Email(message = "Formato de Email inválido")
    private String email;
}
