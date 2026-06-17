package com.sistemadecadastramento.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemadecadastramento.dtos.CategoriaRequestDto;
import com.sistemadecadastramento.dtos.CategoriaResponseDto;
import com.sistemadecadastramento.exceptions.CategoriaJaCadastradaException;
import com.sistemadecadastramento.exceptions.CategoriaNaoEncontradaException;
import com.sistemadecadastramento.models.Categoria;
import com.sistemadecadastramento.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    
    private final CategoriaRepository repository;

    public List<CategoriaResponseDto> listarTodos(){
        List<Categoria> categorias = repository.findAll();
        return categorias.stream().map(categoria -> new CategoriaResponseDto(categoria)).toList();
    }

    public CategoriaResponseDto buscarPorId(Long id){
        Categoria categoria = repository.findById(id)
            .orElseThrow(() -> new CategoriaNaoEncontradaException());
        
        return new CategoriaResponseDto(categoria);
    }

    public CategoriaResponseDto salvarCriar(CategoriaRequestDto dto){
        if(repository.existsByNome(dto.getNome())){
            throw new CategoriaJaCadastradaException();
        }
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setAtivo(true);

        repository.save(categoria);

        return new CategoriaResponseDto(categoria);
        
    }

    public CategoriaResponseDto salvarAtualizar(Long id, CategoriaRequestDto dto){
        Categoria categoriaExistente = repository.findById(id).orElseThrow(() -> new CategoriaNaoEncontradaException());

        categoriaExistente.setNome(dto.getNome());
        categoriaExistente.setDescricao(dto.getDescricao());

        Categoria categoria = repository.save(categoriaExistente);

        return new CategoriaResponseDto(categoria);
    }

    public CategoriaResponseDto alterarAtividade(Long id){
        Categoria categoriaExistente = repository.findById(id).orElseThrow(() -> new CategoriaNaoEncontradaException());

        categoriaExistente.setAtivo(!categoriaExistente.isAtivo());

        Categoria categoria = repository.save(categoriaExistente);

        return new CategoriaResponseDto(categoria);
    }
}
