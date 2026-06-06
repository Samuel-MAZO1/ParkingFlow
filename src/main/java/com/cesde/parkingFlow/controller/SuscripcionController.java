package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.SuscripcionRequestDTO;
import com.cesde.parkingFlow.dto.SuscripcionCancelarRequestDTO;
import com.cesde.parkingFlow.dto.SuscripcionRenovarRequestDTO;
import com.cesde.parkingFlow.dto.response.SuscripcionResponseDTO;
import com.cesde.parkingFlow.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> adquirirSuscripcion(
            @Valid @RequestBody SuscripcionRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SuscripcionResponseDTO response = suscripcionService.adquirirSuscripcion(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<SuscripcionResponseDTO> renovarSuscripcion(
            @PathVariable Long id,
            @Valid @RequestBody SuscripcionRenovarRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SuscripcionResponseDTO response = suscripcionService.renovarSuscripcion(id, request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- NUECO ENDPOINT DE CONSULTA (US-011) ---
    @GetMapping("/mis-suscripciones")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarMisSuscripciones(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<SuscripcionResponseDTO> response = suscripcionService.listarMisSuscripciones(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // --- NUEVO ENDPOINT ADMINISTRATIVO (US-012) ---
    
    @PutMapping("/admin/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuscripcionResponseDTO> cancelarSuscripcion(
            @PathVariable Long id,
            @Valid @RequestBody SuscripcionCancelarRequestDTO request) {
        
        SuscripcionResponseDTO response = suscripcionService.cancelarSuscripcion(id, request);
        return ResponseEntity.ok(response);
    }
}