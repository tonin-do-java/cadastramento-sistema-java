package com.sistemadecadastramento.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemadecadastramento.dtos.ItemVendaRequestDto;
import com.sistemadecadastramento.dtos.VendaRequestDto;
import com.sistemadecadastramento.dtos.VendaResponseDto;
import com.sistemadecadastramento.exceptions.VendaNaoEncontradaException;
import com.sistemadecadastramento.models.ItemVenda;
import com.sistemadecadastramento.models.Status;
import com.sistemadecadastramento.models.Venda;
import com.sistemadecadastramento.repository.VendaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendaService {
    
    private final VendaRepository repository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;

    public List<VendaResponseDto> listarComFiltros(Status status, LocalDateTime dataHora, Long clienteId){
        List<Venda> vendas;

        if(status != null){
            vendas = repository.findByStatus(status);
        } else if(dataHora != null){
            vendas = repository.findByDataHora(dataHora);
        } else if(clienteId != null) {
            vendas = repository.findByClienteId(clienteId);
        } else{
            vendas = repository.findAll();
        }

        return vendas.stream().map(venda -> new VendaResponseDto(venda)).toList();
    }

    public Venda buscarPorId(Long id){
        Venda venda = repository.findById(id)
                    .orElseThrow(() -> new VendaNaoEncontradaException());
        
        return venda;
    }

    public VendaResponseDto salvarCriar(VendaRequestDto dto){

        Venda venda = new Venda();

        venda.setDataHora(LocalDateTime.now());
        venda.setCliente(clienteService.buscarPorId(dto.getClienteId()));
        venda.setVendedor(usuarioService.buscarPorId(dto.getVendedorId()));
        venda.setStatus(dto.getStatus());
        venda.setFormaPagamento(dto.getFormaPagamento());
        BigDecimal descontoVenda = dto.getDesconto() != null ? dto.getDesconto() : BigDecimal.ZERO;
        venda.setDesconto(descontoVenda);
        venda.setObservacao(dto.getObservacao());

        BigDecimal valorSubtotalAcumulado = BigDecimal.ZERO;
        
        for(ItemVendaRequestDto itemDto : dto.getItens()){
            ItemVenda itemReal = new ItemVenda();
            
            BigDecimal precoUnitario = produtoService.buscarId(itemDto.getProdutoId()).getPrecoVenda();

            BigDecimal calculoSub = precoUnitario.multiply(BigDecimal.valueOf(itemDto.getQuantidade())).subtract(itemDto.getDesconto());

            itemReal.setProduto(produtoService.buscarId(itemDto.getProdutoId()));
            itemReal.setQuantidade(itemDto.getQuantidade());
            itemReal.setPrecoUnitario(precoUnitario);
            if(itemDto.getDesconto != null){
                itemReal.setDesconto(itemDto.getDesconto());
            }
            itemReal.setDesconto(BigDecimal.ZERO);
            itemReal.setSubtotal(calculoSub);
            valorSubtotalAcumulado = valorSubtotalAcumulado.add(calculoSub);
            
            venda.adicionarItem(itemReal);
        }

        venda.setValorSubtotal(valorSubtotalAcumulado);

        venda.setValorTotal(valorSubtotalAcumulado.subtract(descontoVenda));
        
        repository.save(venda);

        return new VendaResponseDto(venda);
    }

    public VendaResponseDto alterarStatus(Long id, Status status){
        Venda vendaExistente = buscarPorId(id);

        vendaExistente.setStatus(status);

        repository.save(vendaExistente);

        return new VendaResponseDto(vendaExistente);
    }
}
