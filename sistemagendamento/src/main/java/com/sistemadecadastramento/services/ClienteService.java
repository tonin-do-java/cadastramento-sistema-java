package com.sistemadecadastramento.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemadecadastramento.dtos.ClienteResponseDto;
import com.sistemadecadastramento.dtos.ClienteRequestDto;
import com.sistemadecadastramento.dtos.ContatoDto;
import com.sistemadecadastramento.dtos.EnderecoDto;
import com.sistemadecadastramento.exceptions.UsuarioJaCadastradoException;
import com.sistemadecadastramento.exceptions.UsuarioNaoCadastradoException;
import com.sistemadecadastramento.models.Cliente;
import com.sistemadecadastramento.models.Contato;
import com.sistemadecadastramento.models.Endereco;
import com.sistemadecadastramento.models.TipoPessoa;
import com.sistemadecadastramento.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository repository;


    public List<ClienteResponseDto> listarComFiltros(TipoPessoa tipoPessoa, String cidade){
        List<Cliente> clientes;

        if(tipoPessoa != null){
            clientes = repository.findByTipoPessoa(tipoPessoa);
        } else if(cidade != null){
            clientes = repository.findByEnderecoCidade(cidade);
        } else{
            clientes = repository.findAll();
        }

        return clientes.stream().map(cliente -> new ClienteResponseDto(cliente)).toList();
    }

    public ClienteResponseDto buscarPorId(Long id){
        Cliente cliente = repository.findById(id)
        .orElseThrow(() -> new UsuarioNaoCadastradoException("Esse cliente não existe"));

        return new ClienteResponseDto(cliente);
    }

    public ClienteResponseDto salvarCriar(ClienteRequestDto dto, EnderecoDto end, ContatoDto cont){
        if(repository.existsByDocumento(dto.getDocumento())){
            throw new UsuarioJaCadastradoException("Esse cliente já está cadastrado");
        }

        Endereco endereco = new Endereco(end.getCep(), end.getLogradouro(), end.getNumero(), end.getBairro(), end.getCidade(), end.getEstado());

        Contato contato = new Contato(cont.getTelefone(), cont.getCelular(), cont.getEmail());

        Cliente cliente = new Cliente();

        cliente.setNome(dto.getNome());
        cliente.setTipoPessoa(dto.getTipoPessoa());
        cliente.setDocumento(dto.getDocumento());
        cliente.setNomeFantasia(dto.getNomeFantasia());
        cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        cliente.setEndereco(endereco);
        cliente.setContato(contato);
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDate.now());
        cliente.setDataAtualizacao(LocalDate.now());

        repository.save(cliente);

        return new ClienteResponseDto(cliente);
    }

    public ClienteResponseDto salvarAtualizar(Long id, ClienteRequestDto dto, EnderecoDto end, ContatoDto cont){
        Cliente clienteExistente = repository.findById(id)
        .orElseThrow(() -> new UsuarioNaoCadastradoException("Esse cliente não existe"));

        Endereco endereco = new Endereco(end.getCep(), end.getLogradouro(), end.getNumero(), end.getBairro(), end.getCidade(), end.getEstado());

        Contato contato = new Contato(cont.getTelefone(), cont.getCelular(), cont.getEmail());

        clienteExistente.setNome(dto.getNome());
        clienteExistente.setTipoPessoa(dto.getTipoPessoa());
        clienteExistente.setDocumento(dto.getDocumento());
        clienteExistente.setNomeFantasia(dto.getNomeFantasia());
        clienteExistente.setInscricaoEstadual(dto.getInscricaoEstadual());
        clienteExistente.setEndereco(endereco);
        clienteExistente.setContato(contato);
        clienteExistente.setAtivo(true);
        clienteExistente.setDataAtualizacao(LocalDate.now());

        repository.save(clienteExistente);

        return new ClienteResponseDto(clienteExistente);
    }

    public ClienteResponseDto alterarAtividade(Long id){
        Cliente clienteExistente = repository.findById(id)
        .orElseThrow(() -> new UsuarioNaoCadastradoException("Esse cliente não existe"));

        clienteExistente.setAtivo(!clienteExistente.getAtivo());

        repository.save(clienteExistente);

        return new ClienteResponseDto(clienteExistente);
    }
}
