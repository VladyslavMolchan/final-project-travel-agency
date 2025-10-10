package com.epam.finaltask.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("Invalid role specified");
    }
}