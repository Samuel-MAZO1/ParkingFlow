package com.cesde.parkingFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionCancelarRequestDTO {

    @NotBlank(message = "El motivo de la cancelación es obligatorio")
    @Size(min = 10, max = 255, message = "El motivo debe detallar de forma clara la razón (mínimo 10 caracteres)")
    private String motivo;
}