package com.sistemadecadastramento.exceptions;

public class CategoriaNaoEncontradaException extends RuntimeException {
    public CategoriaNaoEncontradaException(){
        super("Essa categoria não existe");
    }

    public CategoriaNaoEncontradaException(String message) {
        super(message);
    }
    
}
