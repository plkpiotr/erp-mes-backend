package com.herokuapp.erpmesbackend.erpmesbackend.exceptions;

public class EntitiesConflictException extends RuntimeException {

    public EntitiesConflictException() {
        super();
    }

    public EntitiesConflictException(String msg) {
        super(msg);
    }
}
