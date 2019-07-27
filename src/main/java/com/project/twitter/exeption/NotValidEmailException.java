package com.project.twitter.exeption;

public class NotValidEmailException extends RuntimeException {
    public NotValidEmailException(String errorMessage) {
        super(errorMessage);
    }
}