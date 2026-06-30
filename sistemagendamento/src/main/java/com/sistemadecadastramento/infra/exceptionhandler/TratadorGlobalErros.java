package com.sistemadecadastramento.infra.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sistemadecadastramento.exceptions.CamposIncorretosException;
import com.sistemadecadastramento.exceptions.CamposVaziosException;
import com.sistemadecadastramento.exceptions.CategoriaJaCadastradaException;
import com.sistemadecadastramento.exceptions.CategoriaNaoEncontradaException;
import com.sistemadecadastramento.exceptions.ProdutoJaCadastradoException;
import com.sistemadecadastramento.exceptions.ProdutoNaoEncontradoException;
import com.sistemadecadastramento.exceptions.ProdutoPrejuizoException;
import com.sistemadecadastramento.exceptions.SaldoInsuficienteException;
import com.sistemadecadastramento.exceptions.UsuarioJaCadastradoException;
import com.sistemadecadastramento.exceptions.UsuarioNaoCadastradoException;
import com.sistemadecadastramento.exceptions.ValidadeVencidaException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class TratadorGlobalErros{

    @ExceptionHandler(UsuarioJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarUsuarioCadastratado(UsuarioJaCadastradoException ex, HttpServletRequest request){

        ErroResponse erro = new ErroResponse(
            HttpStatus.CONFLICT.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(UsuarioNaoCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarUsuarioInexistente(UsuarioNaoCadastradoException ex, HttpServletRequest request){

        ErroResponse erro = new ErroResponse(
            HttpStatus.NOT_FOUND.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(CamposIncorretosException.class)
    public ResponseEntity<ErroResponse> tratarCampos(CamposIncorretosException ex, HttpServletRequest request){
        ErroResponse erro = new ErroResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(CategoriaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarCategoriaInexistente(CategoriaNaoEncontradaException ex, HttpServletRequest request){
        ErroResponse erro = new ErroResponse(
            HttpStatus.NOT_FOUND.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(CategoriaJaCadastradaException.class)
    public ResponseEntity<ErroResponse> tratarCategoriaCadastrada(CategoriaJaCadastradaException ex, HttpServletRequest request){
        ErroResponse erro = new ErroResponse(
            HttpStatus.CONFLICT.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarProdutoInexistente(ProdutoNaoEncontradoException ex, HttpServletRequest request){
        ErroResponse erro = new ErroResponse(
            HttpStatus.NOT_FOUND.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(ProdutoJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarProdutoCadastrado(ProdutoJaCadastradoException ex, HttpServletRequest request){
        
        ErroResponse erro = new ErroResponse(
            HttpStatus.CONFLICT.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ProdutoPrejuizoException.class)
    public ResponseEntity<ErroResponse> tratarProdutoPrejudicado(ProdutoPrejuizoException ex, HttpServletRequest request){
        
        ErroResponse erro = new ErroResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErroResponse> tratarSaldoInsuficiente(SaldoInsuficienteException ex, HttpServletRequest request){

        ErroResponse erro = new ErroResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);

    }

    @ExceptionHandler(CamposVaziosException.class)
    public ResponseEntity<ErroResponse> tratarCamposVazios(CamposVaziosException ex, HttpServletRequest request){
        
        ErroResponse erro = new ErroResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(ValidadeVencidaException.class)
    public ResponseEntity<ErroResponse> tratarProdutoVencido(ValidadeVencidaException ex, HttpServletRequest request){
        
        ErroResponse erro = new ErroResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

}

record ErroResponse(Integer status, String erro, String caminho){}