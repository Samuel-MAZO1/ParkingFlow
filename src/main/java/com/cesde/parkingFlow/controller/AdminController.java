package com.cesde.parkingFlow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cesde.parkingFlow.dto.ConfigurarCapacidadRequestDTO;
import com.cesde.parkingFlow.dto.EstadoParqueaderoDTO;
import com.cesde.parkingFlow.service.CapacidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

	private final CapacidadService capacidadService; 
  
    @GetMapping("/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstadoParqueaderoDTO> obtenerEstadoTiempoReal() {
        EstadoParqueaderoDTO estado = capacidadService.obtenerEstadoCompleto();
        return ResponseEntity.ok(estado);
    }

  
    @PutMapping("/actualizar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> actualizarCapacidad(
            @Valid @RequestBody ConfigurarCapacidadRequestDTO request) {
        
        capacidadService.configurarCapacidad(request.getTipo(), request.getNuevaCapacidad());
        
        return ResponseEntity.ok("Capacidad para " + request.getTipo() + 
                                " actualizada exitosamente a " + request.getNuevaCapacidad());
    }
    
}
