package com.prjratingsystem.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String text) {
        super(text);
    }

    public CommentNotFoundException() {
        super("Comment not found");
    }
}
