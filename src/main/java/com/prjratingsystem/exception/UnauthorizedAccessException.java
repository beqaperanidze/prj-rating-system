package com.prjratingsystem.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String text) {
        super(text);
    }
}
