package com.sistemadecadastramento.exceptions;

public class ProdutoJaCadastradoException extends RuntimeException {

    public ProdutoJaCadastradoException() {
        super("Esse Produto já está cadastrado");
    }

    public ProdutoJaCadastradoException(String message) {
        super(message);
    }
    
}
