package com.educode.apps.calculadoraaritmetica.services;

import com.educode.apps.calculadoraaritmetica.exceptions.OperationNotFoundException;
import com.educode.apps.calculadoraaritmetica.models.entities.Operation;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import com.educode.apps.calculadoraaritmetica.repositories.OperationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final BigDecimal limit = new BigDecimal("1000000");

    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Transactional
    @Override
    public Operation doOperation(Usuario usuario, Operation operation) {

        BigDecimal result;

        if (Objects.nonNull(operation.getOperandA()) && Objects.nonNull(operation.getOperandB())) {
            validateRange(operation.getOperandA());
            validateRange(operation.getOperandB());
        }

        switch (operation.getOperationEnum()) {
            case ADD:
                result = operation.getOperandA().add(operation.getOperandB());
                break;
            case DIVIDE:
                if (operation.getOperandB().compareTo(BigDecimal.ZERO) == 0)
                    throw new ArithmeticException("Division by zero");
                result = operation.getOperandA().divide(operation.getOperandB(), 2, RoundingMode.HALF_UP);
                break;
            case SUBSTRACT:
                result = operation.getOperandA().subtract(operation.getOperandB());
                break;
            case MULTIPLY:
                result = operation.getOperandA().multiply(operation.getOperandB());
                break;
            case MODULO:
                result = operation.getOperandA().remainder(operation.getOperandB());
                break;
            case POW:
                if (operation.getOperandA().compareTo(BigDecimal.ZERO) < 0)
                    throw new IllegalArgumentException("Cannot sqrt negative number");
                result = BigDecimal.valueOf(Math.sqrt(operation.getOperandA().doubleValue()));
                break;
            default:
                throw new IllegalArgumentException("Operation not found");
        }
        operation.setUsuario(usuario);
        operation.setTimestamp(new Timestamp(System.currentTimeMillis()));
        operation.setResult(result);

        return operationRepository.save(operation);
    }



    @Transactional(readOnly = true)
    @Override
    public List<Operation> history(Long usuarioId) {
        List<Operation> list = this.operationRepository.findByUsuarioId(usuarioId);
        if (list.isEmpty())
            return Collections.emptyList();
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Operation> history(Long usuarioId, Pageable pageable) {
        return this.operationRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Operation operationDetail(Long id) {
        return this.operationRepository.findById(id)
                .orElseThrow(() -> new OperationNotFoundException("Operation not found with user id = "
                        .concat(Long.toString(id))));
    }

    @Transactional
    @Override
    public void deleteOperation(Long id) {
        this.operationRepository.deleteById(id);
    }


    private void validateRange(BigDecimal value) {
        if (value.compareTo(limit.negate()) < 0 || value.compareTo(limit) > 0)
            throw new IllegalArgumentException("Out of range (-1M to 1M)");
    }
}
