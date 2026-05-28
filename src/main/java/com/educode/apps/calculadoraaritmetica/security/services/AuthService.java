package com.educode.apps.calculadoraaritmetica.security.services;

import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationResponse;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterResponse;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;

public interface AuthService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    RegisterResponse register (RegisterRequest registerRequest);
}
