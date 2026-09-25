package com.sistemadecadastramento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemadecadastramento.models.Cliente;
import com.sistemadecadastramento.models.TipoPessoa;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByEnderecoCidade(String cidade);
    List<Cliente> findByTipoPessoa(TipoPessoa tipoPessoa);
    boolean existsByDocumento(String documento);
}
