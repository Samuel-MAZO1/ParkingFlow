package com.cesde.parkingFlow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEntradaAbonadoRequestDTO {

    @NotBlank(message = "La placa del vehículo abonado es obligatoria")
    private String placa;
}