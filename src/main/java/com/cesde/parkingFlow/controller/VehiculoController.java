package com.cesde.parkingFlow.controller;

import com.cesde.parkingFlow.dto.VehiculoRequestDTO;
import com.cesde.parkingFlow.dto.response.VehiculoResponseDTO;
import com.cesde.parkingFlow.service.VehiculoService;
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
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> registrarVehiculo(
            @Valid @RequestBody VehiculoRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // userDetails.getUsername() contiene el email inyectado por tu JwtAuthenticationFilter
        String userEmail = userDetails.getUsername();
        
        VehiculoResponseDTO response = vehiculoService.registrarVehiculo(request, userEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}