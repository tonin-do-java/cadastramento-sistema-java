package com.sistemadecadastramento.dtos;

import java.time.LocalDateTime;

import com.sistemadecadastramento.models.Notificacao;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.TipoNotificacao;

import lombok.Getter;

@Getter
public class NotificacaoResponseDto {
    private Long id;
    private Roles role;
    private String titulo;
    private String texto;
    private LocalDateTime dataHora;
    private Boolean lida;
    private TipoNotificacao tipoNotificacao;

    public NotificacaoResponseDto(Notificacao notificacaoEntity){
        this.id = notificacaoEntity.getId();
        this.role = notificacaoEntity.getRole();
        this.titulo = notificacaoEntity.getTitulo();
        this.texto = notificacaoEntity.getTexto();
        this.dataHora = notificacaoEntity.getDataHora();
        this.lida = notificacaoEntity.getLida();
        this.tipoNotificacao = notificacaoEntity.getTipoNotificacao();
    }
}
