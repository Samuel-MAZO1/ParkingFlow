package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.PlanAbonadoRequestDTO;
import com.cesde.parkingFlow.dto.response.PlanAbonadoResponseDTO;
import com.cesde.parkingFlow.service.PlanAbonadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/planes-abonado")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PlanAbonadoController {

    private final PlanAbonadoService planAbonadoService;

    @PostMapping
    public ResponseEntity<PlanAbonadoResponseDTO> crearPlan(
            @Valid @RequestBody PlanAbonadoRequestDTO request) {
        PlanAbonadoResponseDTO response = planAbonadoService.crearPlan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PlanAbonadoResponseDTO>> listarPlanes(
            @RequestParam(value = "soloActivos", defaultValue = "false") boolean soloActivos) {
        List<PlanAbonadoResponseDTO> response = soloActivos ? 
                planAbonadoService.listarActivos() : 
                planAbonadoService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanAbonadoResponseDTO> actualizarPlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanAbonadoRequestDTO request) {
        PlanAbonadoResponseDTO response = planAbonadoService.actualizarPlan(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarPlan(@PathVariable Long id) {
        planAbonadoService.desactivarPlan(id);
        return ResponseEntity.noContent().build();
    }
}