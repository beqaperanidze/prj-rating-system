package com.prjratingsystem.exception;

public class UserAlreadyExistsException extends Throwable {
    public UserAlreadyExistsException(String text) {
        super(text);
    }
}
