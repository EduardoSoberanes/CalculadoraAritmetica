package com.educode.apps.calculadoraaritmetica.controllers;

import com.educode.apps.calculadoraaritmetica.models.dtos.CalculationDTO;
import com.educode.apps.calculadoraaritmetica.models.entities.Operation;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import com.educode.apps.calculadoraaritmetica.services.OperationService;
import com.educode.apps.calculadoraaritmetica.services.UsuarioService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OperationController {

    private final OperationService operationService;
    private final UsuarioService usuarioService;

    public OperationController(OperationService operationService, UsuarioService usuarioService) {
        this.operationService = operationService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<Operation> calculate(@RequestBody CalculationDTO calculationDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = this.usuarioService.getUsuarioByEmail(authentication.getName());

        Operation operation = new Operation();
        operation.setOperationEnum(calculationDTO.getOperationEnum());
        operation.setOperandA(calculationDTO.getOperandA());
        operation.setOperandB(calculationDTO.getOperandB());
        operation.setUserId(String.valueOf(usuario.getId()));

        return ResponseEntity.ok(this.operationService.doOperation(usuario, operation));
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<Operation> historyById(@PathVariable Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = this.usuarioService.getUsuarioByEmail(authentication.getName());

        Operation operation = this.operationService.operationDetail(id);
        operation.setUserId(String.valueOf(usuario.getId()));

        return ResponseEntity.ok(operation);

    }

    @GetMapping("/history")
    public ResponseEntity<List<Operation>> history(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = this.usuarioService.getUsuarioByEmail(authentication.getName());

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Operation> operationList = this.operationService.history(usuario.getId(), pageable)
                .getContent().stream()
                .map(operation -> setUserIdOperationList(operation, usuario.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(operationList);
    }

    @GetMapping("/history/all")
    public ResponseEntity<List<Operation>> listAll() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = this.usuarioService.getUsuarioByEmail(authentication.getName());

        List<Operation> operationList = this.operationService.history(usuario.getId())
                .stream()
                .map(operation -> setUserIdOperationList(operation, usuario.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(operationList);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        this.operationService.deleteOperation(id);
        return ResponseEntity.noContent().build();
    }

    private Operation setUserIdOperationList(Operation operation, Long userId) {
        operation.setUserId(String.valueOf(userId));
        return operation;
    }
}
