package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.VehiculoRequestDTO;
import com.cesde.parkingFlow.dto.VehiculoUpdateDTO;
import com.cesde.parkingFlow.dto.response.VehiculoResponseDTO;
import com.cesde.parkingFlow.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> registrarVehiculo(
            @Valid @RequestBody VehiculoRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        VehiculoResponseDTO response = vehiculoService.registrarVehiculo(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listarMisVehiculos(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<VehiculoResponseDTO> response = vehiculoService.listarMisVehiculos(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> actualizarVehiculo(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoUpdateDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        VehiculoResponseDTO response = vehiculoService.actualizarVehiculo(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        vehiculoService.eliminarVehiculo(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}