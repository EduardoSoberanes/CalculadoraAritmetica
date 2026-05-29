package com.educode.apps.calculadoraaritmetica.services;

import com.educode.apps.calculadoraaritmetica.clients.EmailClient;
import com.educode.apps.calculadoraaritmetica.exceptions.MailBoxConnectionException;
import com.educode.apps.calculadoraaritmetica.models.dtos.EmailValidationResponse;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EmailValidationService {

    @Value("${api.mailboxlayer.key}")
    private String apiKey;
    private final EmailClient emailClient;

    public EmailValidationService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    public boolean isEmailValid(String email) {
        try {
            EmailValidationResponse response = this.emailClient
                    .checkEmail(this.apiKey, email);

            return this.isValidEmail(response);
        } catch (FeignException e) {
            throw new MailBoxConnectionException(e.getMessage());
        }
    }

    private boolean isValidEmail(EmailValidationResponse response) {
        return Objects.nonNull(response) &&
                response.isFormatValid() &&
                !response.isDisposable() &&
                response.isMxFound();
    }
}
