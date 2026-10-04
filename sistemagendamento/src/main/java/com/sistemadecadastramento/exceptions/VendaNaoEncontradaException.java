package com.sistemadecadastramento.exceptions;

public class VendaNaoEncontradaException extends RuntimeException {

    public VendaNaoEncontradaException() {
        super("Essa venda não existe");
    }

    public VendaNaoEncontradaException(String arg0) {
        super(arg0);
    }
    
}
