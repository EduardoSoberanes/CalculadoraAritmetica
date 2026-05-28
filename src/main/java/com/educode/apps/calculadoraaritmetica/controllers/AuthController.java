package com.educode.apps.calculadoraaritmetica.controllers;

import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationResponse;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterResponse;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import com.educode.apps.calculadoraaritmetica.security.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(this.authService.authenticate(authenticationRequest));
    }
}
