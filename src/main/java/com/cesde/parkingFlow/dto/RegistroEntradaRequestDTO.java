package com.cesde.parkingFlow.dto;

import com.cesde.parkingFlow.enums.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEntradaRequestDTO {

    @NotBlank(message = "La placa del vehículo es obligatoria")
    private String placa;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;
}