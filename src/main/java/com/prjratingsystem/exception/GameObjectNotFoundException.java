package com.prjratingsystem.exception;

public class GameObjectNotFoundException extends RuntimeException {
    public GameObjectNotFoundException(String text) {
        super(text);
    }
}
