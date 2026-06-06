package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.RegistroEntradaAbonadoRequestDTO;
import com.cesde.parkingFlow.dto.RegistroEntradaRequestDTO;
import com.cesde.parkingFlow.dto.RegistroSalidaRequestDTO;
import com.cesde.parkingFlow.dto.response.RecaudoResponseDTO;
import com.cesde.parkingFlow.dto.response.TicketResponseDTO;
import com.cesde.parkingFlow.service.RegistroEstacionamientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estacionamientos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OPERADOR', 'ADMIN')")
public class RegistroEstacionamientoController {

    private final RegistroEstacionamientoService registroEstacionamientoService;

    @PostMapping("/entrada-ocasional")
    public ResponseEntity<TicketResponseDTO> registrarEntradaOcasional(
            @Valid @RequestBody RegistroEntradaRequestDTO request) {
        
        TicketResponseDTO response = registroEstacionamientoService.registrarEntradaOcasional(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- NUEVO ENDPOINT PARA ENTRADA DE ABONADOS (US-014) ---
    @PostMapping("/entrada-abonado")
    public ResponseEntity<TicketResponseDTO> registrarEntradaAbonado(
            @Valid @RequestBody RegistroEntradaAbonadoRequestDTO request) {
        
        TicketResponseDTO response = registroEstacionamientoService.registrarEntradaAbonado(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- NUEVO ENDPOINT PARA SALIDA Y COBRO (US-015) ---
    @PutMapping("/salida")
    public ResponseEntity<RecaudoResponseDTO> registrarSalidaYCobrar(
            @Valid @RequestBody RegistroSalidaRequestDTO request) {
        RecaudoResponseDTO response = registroEstacionamientoService.registrarSalidaYCobrar(request);
        return ResponseEntity.ok(response);
    }
}