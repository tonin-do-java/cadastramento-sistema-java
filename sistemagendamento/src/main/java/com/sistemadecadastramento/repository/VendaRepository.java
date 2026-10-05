package com.sistemadecadastramento.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemadecadastramento.models.Status;
import com.sistemadecadastramento.models.Venda;

@Repository 
public interface VendaRepository extends JpaRepository<Venda, Long>{
    List<Venda> findByStatus(Status status);
    List<Venda> findByDataHora(LocalDateTime dataHora);
    List<Venda> findByClienteId(Long clienteId);
}
