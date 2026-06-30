package com.sistemadecadastramento.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemadecadastramento.dtos.ProdutoRequestDto;
import com.sistemadecadastramento.dtos.ProdutoResponseDto;
import com.sistemadecadastramento.exceptions.CategoriaNaoEncontradaException;
import com.sistemadecadastramento.exceptions.ProdutoJaCadastradoException;
import com.sistemadecadastramento.exceptions.ProdutoNaoEncontradoException;
import com.sistemadecadastramento.exceptions.ProdutoPrejuizoException;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.models.Produto;
import com.sistemadecadastramento.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProdutoService {
    
    private final ProdutoRepository repository;
    private final CategoriaService service;
    private final BigDecimal cem = new BigDecimal("100");

    public List<ProdutoResponseDto> listarComFiltros(String nome, String codigo, Long categoriaId){
        List<Produto> produtos;
        
        if (codigo != null){
            produtos = repository.findByCodigo(codigo);
        } else if(nome != null){
            produtos = repository.findByNomeContainingIgnoreCase(nome);
        } else if(categoriaId != null){
            produtos = repository.findByCategoriaId(categoriaId);
        } else {
            produtos = repository.findAll();
        }

        return produtos.stream().map(produto -> new ProdutoResponseDto(produto)).toList();
    }

    public ProdutoResponseDto buscarPorId(Long id){
        Produto produto = repository.findById(id)
        .orElseThrow(() -> new ProdutoNaoEncontradoException());

        return new ProdutoResponseDto(produto);
    }

    public Produto buscarId(Long id){
        Produto produto = repository.findById(id)
        .orElseThrow(() -> new ProdutoNaoEncontradoException());

        return produto;
    }

    public ProdutoResponseDto salvarCriar(ProdutoRequestDto dto){
        
        Categoria categoriaExistente = service.buscarIdCategoria(dto.getCategoriaId());

        if(categoriaExistente.isAtivo() == false){
            throw new CategoriaNaoEncontradaException("Essa categoria está desativada");
        }

        if(repository.existsByCodigo(dto.getCodigo())){
            throw new ProdutoJaCadastradoException();
        }

        if(dto.getPrecoVenda().compareTo(dto.getPrecoCusto()) <= 0){
            throw new ProdutoPrejuizoException();
        }
        
        BigDecimal lucroBruto = dto.getPrecoVenda().subtract(dto.getPrecoCusto());
        BigDecimal margemCalculada = lucroBruto.divide(dto.getPrecoVenda(), 4, RoundingMode.HALF_UP).multiply(cem).setScale(2, RoundingMode.HALF_UP);

        Produto produto = new Produto();
        produto.setCategoria(categoriaExistente);
        produto.setCodigo(dto.getCodigo());
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setMarca(dto.getMarca());
        produto.setUnidadeMedida(dto.getUnidadeMedida());
        produto.setPrecoCusto(dto.getPrecoCusto());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setMargemLucro(margemCalculada);
        produto.setEstoqueMinimo(dto.getEstoqueMinimo());
        produto.setEstoqueMaximo(dto.getEstoqueMaximo());
        produto.setControlaLote(dto.getControlaLote());
        produto.setControlaValidade(dto.getControlaValidade());
        produto.setAtivo(true);

        repository.save(produto);

        return new ProdutoResponseDto(produto);
        
    }

    public ProdutoResponseDto salvarAtualizar(Long id, ProdutoRequestDto dto){
        Produto produtoExistente = repository.findById(id)
        .orElseThrow(() -> new ProdutoNaoEncontradoException());

        Categoria categoriaExistente = service.buscarIdCategoria(dto.getCategoriaId());

        if(categoriaExistente.isAtivo() == false){
            throw new CategoriaNaoEncontradaException("Essa categoria está desativada");
        }

        BigDecimal lucroBruto = dto.getPrecoVenda().subtract(dto.getPrecoCusto());
        BigDecimal margemCalculada = lucroBruto.divide(dto.getPrecoVenda(), 4, RoundingMode.HALF_UP).multiply(cem).setScale(2, RoundingMode.HALF_UP);


        produtoExistente.setCategoria(categoriaExistente);
        produtoExistente.setCodigo(dto.getCodigo());
        produtoExistente.setNome(dto.getNome());
        produtoExistente.setDescricao(dto.getDescricao());
        produtoExistente.setMarca(dto.getMarca());
        produtoExistente.setUnidadeMedida(dto.getUnidadeMedida());
        produtoExistente.setPrecoCusto(dto.getPrecoCusto());
        produtoExistente.setPrecoVenda(dto.getPrecoVenda());
        produtoExistente.setMargemLucro(margemCalculada);
        produtoExistente.setEstoqueMinimo(dto.getEstoqueMinimo());
        produtoExistente.setEstoqueMaximo(dto.getEstoqueMaximo());
        produtoExistente.setControlaLote(dto.getControlaLote());
        produtoExistente.setControlaValidade(dto.getControlaValidade());

        repository.save(produtoExistente);

        return new ProdutoResponseDto(produtoExistente);
    }

    public ProdutoResponseDto alterarAtividade(Long id){
        Produto produtoExistente = repository.findById(id)
        .orElseThrow(() -> new ProdutoNaoEncontradoException());

        produtoExistente.setAtivo(!produtoExistente.getAtivo());

        repository.save(produtoExistente);

        return new ProdutoResponseDto(produtoExistente);
    }
}
