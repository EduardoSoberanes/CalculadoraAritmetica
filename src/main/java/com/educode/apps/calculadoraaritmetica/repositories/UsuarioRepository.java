package com.educode.apps.calculadoraaritmetica.repositories;

import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

}
