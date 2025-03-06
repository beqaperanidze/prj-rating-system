package com.prjratingsystem.exception;

public class UserAlreadyExistException extends Throwable {
    public UserAlreadyExistException(String text) {
        super(text);
    }
}
