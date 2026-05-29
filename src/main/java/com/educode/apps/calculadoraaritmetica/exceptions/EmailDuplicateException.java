package com.educode.apps.calculadoraaritmetica.exceptions;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException(String emailAlreadyRegistered) {
        super(emailAlreadyRegistered);
    }
}
