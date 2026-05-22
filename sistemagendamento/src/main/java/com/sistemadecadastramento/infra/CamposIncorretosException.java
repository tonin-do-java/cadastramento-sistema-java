package com.sistemadecadastramento.infra;

public class CamposIncorretosException extends RuntimeException{
    public CamposIncorretosException(){
        System.out.println("Os campos estão incorretos!");
    }

    public CamposIncorretosException(String message) {
        super(message);
    }
    
}
