package com.cesde.parkingFlow.dto.response;

import com.cesde.parkingFlow.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponseDTO {
    private Long id;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private String marca;
    private String modelo;
    private String color;
    private Long userId;
}