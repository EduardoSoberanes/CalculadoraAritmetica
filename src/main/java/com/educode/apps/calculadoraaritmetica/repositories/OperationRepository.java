package com.educode.apps.calculadoraaritmetica.repositories;

import com.educode.apps.calculadoraaritmetica.models.entities.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByUsuarioId(Long usuarioId);
    Page<Operation> findByUsuarioId(Long usuarioId, Pageable pageable);
    Optional<Operation> findById(Long id);
}
