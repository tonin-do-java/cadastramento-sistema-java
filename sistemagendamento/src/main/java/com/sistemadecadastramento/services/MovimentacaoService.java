package com.sistemadecadastramento.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sistemadecadastramento.dtos.MovimentacaoRequestDto;
import com.sistemadecadastramento.dtos.MovimentacaoResponseDto;
import com.sistemadecadastramento.exceptions.CamposVaziosException;
import com.sistemadecadastramento.exceptions.ProdutoNaoEncontradoException;
import com.sistemadecadastramento.exceptions.SaldoInsuficienteException;
import com.sistemadecadastramento.exceptions.ValidadeVencidaException;
import com.sistemadecadastramento.models.MovimentacaoEstoque;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.models.TipoMovimentacao;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.repository.MovimentacaoRepository;
import com.sistemadecadastramento.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MovimentacaoService {
    
    private MovimentacaoRepository repository;
    private ProdutoRepository produtoRepository;
    private ProdutoService produtoService;
    private UsuarioService usuarioService;

    public List<MovimentacaoResponseDto> listarHistorico(){
        List<MovimentacaoEstoque> movimentacoes = repository.findAll();

        return movimentacoes.stream().map(movimentacao -> new MovimentacaoResponseDto(movimentacao)).toList();
    }
    
    public List<MovimentacaoResponseDto> listarHistoricoPorProduto(Long produtoId){
        if(!produtoRepository.existsById(produtoId)){
            throw new ProdutoNaoEncontradoException();
        }

        List<MovimentacaoEstoque> movimentacoes = repository.findByProdutoIdOrderByDataHoraDesc(produtoId);

        return movimentacoes.stream().map(movimentacao -> new MovimentacaoResponseDto(movimentacao)).toList();
    }

    public MovimentacaoResponseDto registrarMovimentacao(MovimentacaoRequestDto dto){
        Produto produto = produtoService.buscarId(dto.getProdutoId());

        validarLoteValidade(produto, dto);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();

        Usuario usuarioLogado = usuarioService.buscarPorEmail(emailUsuarioLogado);

        if(dto.getTipo() == TipoMovimentacao.ENTRADA){
            produto.setQuantidadeAtual(produto.getQuantidadeAtual() + dto.getQuantidade());
        } else{
            if(produto.getQuantidadeAtual() < dto.getQuantidade()){
                throw new SaldoInsuficienteException();
            }
            produto.setQuantidadeAtual(produto.getQuantidadeAtual() - dto.getQuantidade());
        }

        produtoRepository.save(produto);

        MovimentacaoEstoque novaMovimentacao = new MovimentacaoEstoque();
        novaMovimentacao.setProduto(produto);
        novaMovimentacao.setUsuario(usuarioLogado);
        novaMovimentacao.setTipoMovimentacao(dto.getTipo());
        novaMovimentacao.setOrigemMovimentacao(dto.getOrigem());
        novaMovimentacao.setDataHora(LocalDateTime.now());
        novaMovimentacao.setLote(dto.getLote());
        novaMovimentacao.setValidade(dto.getDataValidade());

        repository.save(novaMovimentacao);

        return new MovimentacaoResponseDto(novaMovimentacao);
    }

    private void validarLoteValidade(Produto produto, MovimentacaoRequestDto dto){
        if(produto.getControlaLote() && (dto.getLote() == null || dto.getLote().isBlank())){
            throw new CamposVaziosException("O lote é obrigatório para esse produto");
        }
        if(produto.getControlaValidade() && dto.getDataValidade() == null){
            throw new CamposVaziosException("A validade é obrigatória para esse produto");
        }
        if(dto.getDataValidade() != null && dto.getDataValidade().isBefore(LocalDate.now())){
            throw new ValidadeVencidaException();
        }
    }
}