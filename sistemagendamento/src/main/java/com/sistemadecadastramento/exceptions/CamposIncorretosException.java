package com.sistemadecadastramento.exceptions;

public class CamposIncorretosException extends RuntimeException{
    public CamposIncorretosException(){
        super("Os campos estão incorretos!");
    }

    public CamposIncorretosException(String message) {
        super(message);
    }
    
}
