package com.sistemadecadastramento.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContatoDto {
    
    @Pattern(regexp = "^$|^[1-9]{2}\\d{4}-?\\d{4}$")
    private String telefone;
    
    @NotBlank(message = "numero de celular é obrigatório")
    @Pattern(regexp = "^[1-9]{2}9\\d{4}-?\\d{4}$")
    private String celular;
    
    @Email(message = "O formato de Email é inválido")
    private String email;
}
