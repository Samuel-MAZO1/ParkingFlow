package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.SuscripcionRequestDTO;
import com.cesde.parkingFlow.dto.response.SuscripcionResponseDTO;
import com.cesde.parkingFlow.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}