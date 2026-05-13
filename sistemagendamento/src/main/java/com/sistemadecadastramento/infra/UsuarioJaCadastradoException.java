package com.sistemadecadastramento.infra;

public class UsuarioJaCadastradoException extends RuntimeException{

    public UsuarioJaCadastradoException() {
        System.out.println("O usuario que usa esse email, já está cadastrado!");
    }
    
}