package com.cesde.parkingFlow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSalidaRequestDTO {

    @NotBlank(message = "La placa o el número de ticket es obligatorio")
    private String placaOTicket;

    @NotBlank(message = "El método de pago es obligatorio (Ejem: EFECTIVO, NEQUI, TARJETA)")
    private String metodoPago;
}