package com.herokuapp.erpmesbackend.erpmesbackend.exceptions;

public class NotAManagerException extends RuntimeException {

    public NotAManagerException() {
        super();
    }

    public NotAManagerException(String message) {
        super(message);
    }
}
