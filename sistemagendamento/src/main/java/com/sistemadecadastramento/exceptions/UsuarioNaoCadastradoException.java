package com.sistemadecadastramento.exceptions;

public class UsuarioNaoCadastradoException extends RuntimeException {
    
    public UsuarioNaoCadastradoException(){
        super("Este usuário não existe!");
    }

    public UsuarioNaoCadastradoException(String message) {
        super(message);
    }
    
}
