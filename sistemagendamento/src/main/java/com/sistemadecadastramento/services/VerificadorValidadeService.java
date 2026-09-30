package com.sistemadecadastramento.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadecadastramento.dtos.ValidadeProximaEvent;
import com.sistemadecadastramento.models.MovimentacaoEstoque;
import com.sistemadecadastramento.models.TipoNotificacao;
import com.sistemadecadastramento.repository.NotificacaoRepository;
import com.sistemadecadastramento.services.MovimentacaoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificadorValidadeService {
    
    private final MovimentacaoService movimentacaoService;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificacaoRepository repository;

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Sao_Paulo")
    //@Scheduled(fixedRate = 10000)//
    @Transactional
    public void verificarValidadesProximas(){
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);
        
        List<MovimentacaoEstoque> lotesAVencer = movimentacaoService.buscarLotesAVencer(hoje, limite);

        for(MovimentacaoEstoque movimentacao : lotesAVencer){
            LocalDate dataValidade = movimentacao.getValidade();
            if(dataValidade == null) continue;

            boolean jaNotificado = repository.existsByTipoNotificacaoAndIdReferencia(TipoNotificacao.VALIDADE, movimentacao.getId());

            if(jaNotificado) continue;

            long diasRestantes = ChronoUnit.DAYS.between(hoje, dataValidade);

            eventPublisher.publishEvent(new ValidadeProximaEvent(movimentacao.getProduto(), diasRestantes, movimentacao.getId()));
        }
    }
}
