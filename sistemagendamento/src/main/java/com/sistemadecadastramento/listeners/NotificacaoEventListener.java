package com.sistemadecadastramento.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

import com.sistemadecadastramento.controllers.NotificacaoController;

import com.sistemadecadastramento.dtos.EstoqueBaixoEvent;
import com.sistemadecadastramento.dtos.ValidadeProximaEvent;
import com.sistemadecadastramento.dtos.NotificacaoRequestDto;

import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.TipoNotificacao;

@Component
@RequiredArgsConstructor
public class NotificacaoEventListener {
    
    private final NotificacaoController controle;

    @Async
    @TransactionalEventListener
    public void processarEstoqueBaixo(EstoqueBaixoEvent event){
        Produto produto = event.getProduto();

        NotificacaoRequestDto dto = new NotificacaoRequestDto();
        dto.setRole(Roles.ADMIN);
        dto.setTitulo("Estoque Crítico: " + produto.getCodigo());
        dto.setTexto(String.format("O produto '%s' atingiu o limite crítico. Restam apenas %d unidades no estoque (Mínimo exigido: %d).", 
            produto.getNome(), 
            produto.getQuantidadeAtual(), 
            produto.getEstoqueMinimo()));
        dto.setTipoNotificacao(TipoNotificacao.ESTOQUE);
        dto.setIdReferencia(event.getMovimentacaoId());

        controle.criarNotificacao(dto);

    }

    @Async
    @TransactionalEventListener
    public void processarValidadeProxima(ValidadeProximaEvent event){
        Produto produto = event.getProduto();
        long dias = event.getDiasRestantes();

        NotificacaoRequestDto dto = new NotificacaoRequestDto();
        dto.setRole(Roles.ADMIN);
        dto.setTitulo("Aviso de Validade: " + produto.getCodigo());
        dto.setTexto(String.format("O produto '%s' vence em %d dias. Verifique o estoque.", 
            produto.getNome(), dias));
        dto.setTipoNotificacao(TipoNotificacao.VALIDADE);
        dto.setIdReferencia(event.getMovimentacaoId());

        controle.criarNotificacao(dto);
    }
}
