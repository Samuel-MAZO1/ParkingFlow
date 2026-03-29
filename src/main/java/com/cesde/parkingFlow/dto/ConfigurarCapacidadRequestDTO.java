package com.cesde.parkingFlow.dto;

import com.cesde.parkingFlow.enums.TipoVehiculo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfigurarCapacidadRequestDTO {
    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipo;
    
    @Min(value = 0, message = "La capacidad no puede ser negativa")
    @NotNull(message = "La nueva capacidad es obligatoria")
    private Integer nuevaCapacidad;
}