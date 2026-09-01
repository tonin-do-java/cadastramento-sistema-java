package com.sistemadecadastramento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sistemadecadastramento.models.Notificacao;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long>{
    List<Notificacao> findByLidaFalseOrderByDataHoraDesc();

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = false WHERE n.lida = true")
    void marcarTodasLidas();
}
