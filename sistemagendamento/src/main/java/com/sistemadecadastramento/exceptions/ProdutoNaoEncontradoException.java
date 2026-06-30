package com.sistemadecadastramento.exceptions;

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException() {
        super("Esse produto não existe!");
    }

    public ProdutoNaoEncontradoException(String message) {
        super(message);
    }
    
}
