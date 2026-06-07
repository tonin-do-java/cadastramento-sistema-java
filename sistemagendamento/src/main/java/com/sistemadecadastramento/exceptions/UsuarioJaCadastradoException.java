package com.sistemadecadastramento.exceptions;

public class UsuarioJaCadastradoException extends RuntimeException{

    public UsuarioJaCadastradoException() {
        super("O usuario que usa esse email, já está cadastrado!");
    }

    public UsuarioJaCadastradoException(String message) {
        super(message);
    }
    
}