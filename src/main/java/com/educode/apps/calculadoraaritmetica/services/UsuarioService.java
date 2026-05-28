package com.educode.apps.calculadoraaritmetica.services;

import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;

public interface UsuarioService {
    Usuario getUsuarioByEmail(String email);
}
