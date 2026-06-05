package com.cesde.parkingFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionRenovarRequestDTO {

    @NotNull(message = "El ID del plan es obligatorio para la renovación")
    private Long planId;

    @NotBlank(message = "La referencia de pago es obligatoria")
    private String referenciaPago;
}