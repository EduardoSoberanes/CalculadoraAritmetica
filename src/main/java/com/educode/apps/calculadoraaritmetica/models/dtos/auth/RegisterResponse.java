package com.educode.apps.calculadoraaritmetica.models.dtos.auth;

import com.educode.apps.calculadoraaritmetica.models.dtos.UsuarioDTO;

public class RegisterResponse {

    private String message;
    private UsuarioDTO usuarioDTO;

    public RegisterResponse() {
    }

    public RegisterResponse(String message, UsuarioDTO usuarioDTO) {
        this.message = message;
        this.usuarioDTO = usuarioDTO;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UsuarioDTO getUsuarioDTO() {
        return usuarioDTO;
    }

    public void setUsuarioDTO(UsuarioDTO usuarioDTO) {
        this.usuarioDTO = usuarioDTO;
    }
}
