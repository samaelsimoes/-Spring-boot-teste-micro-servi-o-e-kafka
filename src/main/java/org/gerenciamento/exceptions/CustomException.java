package org.gerenciamento.exceptions;

public class CustomException extends RuntimeException {

    private String code;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}