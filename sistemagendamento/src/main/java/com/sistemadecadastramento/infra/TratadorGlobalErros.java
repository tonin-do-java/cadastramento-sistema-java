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
}

record ErroResponse(Integer status, String erro, String caminho){}