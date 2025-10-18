package com.epam.finaltask.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String string) {
        super("User with this username or email already exists");
    }
}