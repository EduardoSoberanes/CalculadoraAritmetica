package com.educode.apps.calculadoraaritmetica.controllers;

import com.educode.apps.calculadoraaritmetica.exceptions.EmailDuplicateException;
import com.educode.apps.calculadoraaritmetica.exceptions.EmailInvalidException;
import com.educode.apps.calculadoraaritmetica.exceptions.MailBoxConnectionException;
import com.educode.apps.calculadoraaritmetica.exceptions.OperationNotFoundException;
import com.educode.apps.calculadoraaritmetica.models.dtos.ErrorDTO;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler(EmailInvalidException.class)
    public ResponseEntity<ErrorDTO> emailInvalid(Exception ex) {

        ErrorDTO error = new ErrorDTO();
        error.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.setMessage(ex.getMessage());
        error.setDetails("Disposable email addresses are not allowed");

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ErrorDTO> emailDuplicated(Exception ex) {

        ErrorDTO error = new ErrorDTO();
        error.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.setMessage(ex.getMessage());
        error.setDetails("Email already exist in the database");

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MailBoxConnectionException.class)
    public ResponseEntity<ErrorDTO> mailBoxConnectionException(Exception ex) {
        ErrorDTO error = new ErrorDTO();
        error.setStatus(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
        error.setMessage(ex.getMessage());
        error.setDetails("Service Mailboxlayer unavailable");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDTO> badCredentials(Exception ex) {
        ErrorDTO error = new ErrorDTO();
        error.setStatus(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        error.setMessage(ex.getMessage());
        error.setDetails("Invalid email or password. Please try again");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(OperationNotFoundException.class)
    public ResponseEntity<ErrorDTO> operationNotFound(Exception ex) {
        ErrorDTO error = new ErrorDTO();
        error.setStatus(String.valueOf(HttpStatus.NOT_FOUND.value()));
        error.setMessage(ex.getMessage());
        error.setDetails("Operation not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({ArithmeticException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorDTO> illegalArguments(Exception ex) {
        ErrorDTO error = new ErrorDTO();
        error.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.setMessage(ex.getMessage());
        error.setDetails("Illegal arguments");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


}
