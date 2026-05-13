package com.sistemadecadastramento.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(UsuarioNaoExisteException.class)
    public ResponseEntity<ErroResponse> tratarUsuarioInexistente(UsuarioNaoExisteException ex, HttpServletRequest request){

        ErroResponse erro = new ErroResponse(
            HttpStatus.NOT_FOUND.value(), 
            ex.getMessage(), 
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    
}

record ErroResponse(Integer status, String erro, String caminho){}