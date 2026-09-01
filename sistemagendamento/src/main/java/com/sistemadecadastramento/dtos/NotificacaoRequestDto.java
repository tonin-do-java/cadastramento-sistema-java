package com.sistemadecadastramento.dtos;

import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.TipoNotificacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoRequestDto {
    @NotNull(message = "esse campo não pode ser nulo")
    private Roles role;

    @NotBlank(message = "o título precisa ser preenchido")
    private String titulo;

    @NotBlank(message = "o texto precisa ser preenchido")
    private String texto;

    @NotNull(message = "precisa de preencher o tipo de notificação")
    private TipoNotificacao tipoNotificacao;

}
