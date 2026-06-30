package com.sistemadecadastramento.exceptions;

public class CamposVaziosException extends RuntimeException{

    public CamposVaziosException() {
        super("Esse campo é obrigatório estar preenchido");
    }

    public CamposVaziosException(String message) {
        super(message);
    }
    
}
