package com.sistemadecadastramento.exceptions;

public class CategoriaJaCadastradaException extends RuntimeException {
    public CategoriaJaCadastradaException(){
        super("Essa categoria já está cadastrada");
    }

    public CategoriaJaCadastradaException(String message) {
        super(message);
    }
}
