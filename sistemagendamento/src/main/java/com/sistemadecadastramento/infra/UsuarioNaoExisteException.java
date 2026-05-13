package com.sistemadecadastramento.infra;

public class UsuarioNaoExisteException extends RuntimeException {
    
    public UsuarioNaoExisteException(){
        System.out.println("Este usuário não existe!");
    }
}
