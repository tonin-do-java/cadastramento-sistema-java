package com.sistemadecadastramento.exceptions;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException() {
        super("Saldo Insuficiente!");
    }

    public SaldoInsuficienteException(String message) {
        super(message);
    }
    
}
