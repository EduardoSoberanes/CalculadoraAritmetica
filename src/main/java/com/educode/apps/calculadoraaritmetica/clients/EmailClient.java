package com.educode.apps.calculadoraaritmetica.clients;

import com.educode.apps.calculadoraaritmetica.models.dtos.EmailValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mailboxlayer-client", url = "https://apilayer.net")
public interface EmailClient {

    @GetMapping("/api/check")
    EmailValidationResponse checkEmail(
            @RequestParam("access_key") String apiKey,
            @RequestParam("email") String email
    );
}