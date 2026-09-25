package com.sistemadecadastramento.dtos;

import java.time.LocalDate;

import com.sistemadecadastramento.models.Cliente;
import com.sistemadecadastramento.models.Endereco;
import com.sistemadecadastramento.models.Contato;
import com.sistemadecadastramento.models.TipoPessoa;

import lombok.Getter;

@Getter
public class ClienteResponseDto{
    private Long id;
    private TipoPessoa tipoPessoa;
    private String nome;
    private String documento;
    private Endereco endereco;
    private Contato contato;
    private Boolean ativo;
    private LocalDate dataCadastro;
    private LocalDate dataAtualizacao;
    private String nomeFantasia;
    private String inscricaoEstadual;

    public ClienteResponseDto(Cliente clienteEntity) {
        this.id = clienteEntity.getId();
        this.tipoPessoa = clienteEntity.getTipoPessoa();
        this.nome = clienteEntity.getNome();
        this.documento = clienteEntity.getDocumento();
        this.endereco = clienteEntity.getEndereco();
        this.contato = clienteEntity.getContato();
        this.ativo = clienteEntity.getAtivo();
        this.dataCadastro = clienteEntity.getDataCadastro();
        this.dataAtualizacao = clienteEntity.getDataAtualizacao();
        this.nomeFantasia = clienteEntity.getNomeFantasia();
        this.inscricaoEstadual = clienteEntity.getInscricaoEstadual();
    }
    
}
