package com.sistemadecadastramento.exceptions;

public class ValidadeVencidaException extends RuntimeException{

    public ValidadeVencidaException() {
        super("Não pode movimentar Produto vencido");
    }

    public ValidadeVencidaException(String message) {
        super(message);
    }
}
