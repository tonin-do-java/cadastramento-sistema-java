package com.sistemadecadastramento.exceptions;

public class ProdutoPrejuizoException extends RuntimeException{

    public ProdutoPrejuizoException() {
        super("Preço de venda deve ser maior que o preço de custo");
    }

    public ProdutoPrejuizoException(String message) {
        super(message);
    }
    
}
