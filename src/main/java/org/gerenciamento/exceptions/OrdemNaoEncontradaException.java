package org.gerenciamento.exceptions;

public class OrdemNaoEncontradaException extends CustomException {

    public OrdemNaoEncontradaException(String message) {
        super(message);
    }

    public OrdemNaoEncontradaException(String code, String message) {
        super(code, message);
    }
}