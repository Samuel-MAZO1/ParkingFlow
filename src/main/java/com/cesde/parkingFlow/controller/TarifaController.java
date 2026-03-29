package com.cesde.parkingFlow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cesde.parkingFlow.dto.TarifaRequestDTO;
import com.cesde.parkingFlow.dto.response.TarifaResponseDTO;
import com.cesde.parkingFlow.service.TarifaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tarifas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") 
public class TarifaController {

    private final TarifaService tarifaService;

    @PostMapping("/crear")
    public ResponseEntity<TarifaResponseDTO> crear(@Valid @RequestBody TarifaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaService.crearTarifa(dto));
    }

    @GetMapping("/todas")
    public ResponseEntity<List<TarifaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(tarifaService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaResponseDTO> obtenerUno(@PathVariable Long id) {
        return ResponseEntity.ok(tarifaService.obtenerPorId(id));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<TarifaResponseDTO> actualizar(@PathVariable Long id, @RequestBody TarifaRequestDTO dto) {
        return ResponseEntity.ok(tarifaService.actualizarTarifa(id, dto));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        tarifaService.desactivarTarifa(id);
        return ResponseEntity.noContent().build();
    }
}