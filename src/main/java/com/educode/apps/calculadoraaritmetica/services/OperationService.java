package com.educode.apps.calculadoraaritmetica.services;

import com.educode.apps.calculadoraaritmetica.models.entities.Operation;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OperationService {

    Operation doOperation(Usuario usuario, Operation operation);
    List<Operation> history(Long usuarioId);
    Page<Operation> history(Long usuarioId, Pageable pageable);
    Operation operationDetail(Long id);
    void deleteOperation(Long id);
}
