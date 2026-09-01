package com.sistemadecadastramento.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemadecadastramento.dtos.NotificacaoRequestDto;
import com.sistemadecadastramento.dtos.NotificacaoResponseDto;
import com.sistemadecadastramento.exceptions.NotificacaoNaoExistenteException;
import com.sistemadecadastramento.models.Notificacao;
import com.sistemadecadastramento.repository.NotificacaoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificacaoService {
    private final NotificacaoRepository repository;

    public List<NotificacaoResponseDto> listarNotificacao(){
        List<Notificacao> notificacoes = repository.findAll();
        
        return notificacoes.stream().map(notificacao -> new NotificacaoResponseDto(notificacao)).toList();
    }

    public List<NotificacaoResponseDto> listarNotNaoLidas(){
        List<Notificacao> notificacoes = repository.findByLidaFalseOrderByDataHoraDesc();

        return notificacoes.stream().map(notificacao -> new NotificacaoResponseDto(notificacao)).toList();
    }

    public NotificacaoResponseDto lerMensagem(Long id){
        Notificacao notificacaoExistente = repository.findById(id)
        .orElseThrow(() -> new NotificacaoNaoExistenteException());

        notificacaoExistente.setLida(true);

        repository.save(notificacaoExistente);

        return new NotificacaoResponseDto(notificacaoExistente);
    }

    public void lerTodasMensagens(){
        repository.marcarTodasLidas();
    }

    public NotificacaoResponseDto salvarCriar(NotificacaoRequestDto dto){
        Notificacao notificacao = new Notificacao();
        notificacao.setRole(dto.getRole());
        notificacao.setTitulo(dto.getTitulo());
        notificacao.setTexto(dto.getTexto());
        notificacao.setLida(false);
        notificacao.setDataHora(LocalDateTime.now());
        notificacao.setTipoNotificacao(dto.getTipoNotificacao());

        repository.save(notificacao);

        return new NotificacaoResponseDto(notificacao);
    }
}
