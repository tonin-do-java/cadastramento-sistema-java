package com.sistemadecadastramento.exceptions;

public class NotificacaoNaoExistenteException extends RuntimeException {

    public NotificacaoNaoExistenteException() {
        super("Essa notificação não existe");
    }

    public NotificacaoNaoExistenteException(String message) {
        super(message);
    }
    
}
