package com.sistemadecadastramento.infra;

public class UsuarioNaoCadastradoException extends RuntimeException {
    
    public UsuarioNaoCadastradoException(){
        System.out.println("Este usuário não existe!");
    }

    public UsuarioNaoCadastradoException(String message) {
        super(message);
    }
    
}
