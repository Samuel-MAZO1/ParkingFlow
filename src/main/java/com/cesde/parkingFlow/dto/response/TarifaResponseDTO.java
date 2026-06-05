package com.cesde.parkingFlow.dto.response;

import java.time.LocalTime;

import com.cesde.parkingFlow.enums.TipoVehiculo;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TarifaResponseDTO {
    private Long id;
    private TipoVehiculo tipoVehiculo;
    private Double valorHora;
    private Double valorDiaCompleto;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;
    private Boolean esFestivo;
    private Boolean activo;
}