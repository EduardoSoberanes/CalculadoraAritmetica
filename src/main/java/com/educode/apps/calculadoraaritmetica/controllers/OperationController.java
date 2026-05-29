package com.educode.apps.calculadoraaritmetica.controllers;

import com.educode.apps.calculadoraaritmetica.models.dtos.CalculationDTO;
import com.educode.apps.calculadoraaritmetica.models.entities.Operation;
import com.educode.apps.calculadoraaritmetica.models.entities.Usuario;
import com.educode.apps.calculadoraaritmetica.services.OperationService;
import com.educode.apps.calculadoraaritmetica.services.UsuarioService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (operation.getUserId().equals(String.valueOf(usuario.getId())))
            return ResponseEntity.ok(operation);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping("/history")
    public ResponseEntity<List<Operation>> getHistory(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Usuario usuario = usuarioService.getUsuarioByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        List<Operation> operations;

        if (page != null && size != null) {
            Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            operations = operationService.history(usuario.getId(), pageable).getContent();
        } else {
            operations = operationService.history(usuario.getId());
        }

        return ResponseEntity.ok(operations);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        this.operationService.deleteOperation(id);
        return ResponseEntity.noContent().build();
    }

}
