package com.educode.apps.calculadoraaritmetica.security.services;

import com.educode.apps.calculadoraaritmetica.exceptions.EmailInvalidException;
import com.educode.apps.calculadoraaritmetica.models.dtos.UsuarioDTO;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.AuthenticationResponse;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterRequest;
import com.educode.apps.calculadoraaritmetica.models.dtos.auth.RegisterResponse;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import com.educode.apps.calculadoraaritmetica.repositories.UsuarioRepository;
import com.educode.apps.calculadoraaritmetica.security.JwtProvider;
import com.educode.apps.calculadoraaritmetica.services.EmailValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class AuthServiceImpl implements AuthService{

    private final String defaultPassword;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UsuarioRepository usuarioRepository;
    private final EmailValidationService emailValidationService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager
            , JwtProvider jwtProvider
            , UsuarioRepository usuarioRepository
            , EmailValidationService emailValidationService
            , PasswordEncoder passwordEncoder
            , @Value("${application.security.password-default}") String defaultPassword) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.usuarioRepository = usuarioRepository;
        this.emailValidationService = emailValidationService;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        String password = authenticationRequest.getPassword().concat(this.defaultPassword);
        authenticationRequest.setPassword(password);

        Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );
        return new AuthenticationResponse(jwtProvider.generateToken(auth));
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (!emailValidationService.isEmailValid(registerRequest.getEmail()))
            throw new EmailInvalidException("Email validation failed");

        String password = registerRequest.getPassword().concat(defaultPassword);

        Usuario usuarioRegister = new Usuario();
        usuarioRegister.setUsername(registerRequest.getUsername());
        usuarioRegister.setEmail(registerRequest.getEmail());
        usuarioRegister.setPassword(this.passwordEncoder.encode(password));
        usuarioRegister.setCreateAt(new Timestamp(System.currentTimeMillis()));

        usuarioRegister = this.usuarioRepository.save(usuarioRegister);

        UsuarioDTO usuarioDTO = new UsuarioDTO(usuarioRegister.getUsername()
                , usuarioRegister.getEmail(), usuarioRegister.getCreateAt());

        return new RegisterResponse("You have successfully registered!"
                , usuarioDTO);
    }

}
