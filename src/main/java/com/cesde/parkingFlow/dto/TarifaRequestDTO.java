package com.cesde.parkingFlow.dto;

import java.time.LocalTime;

import com.cesde.parkingFlow.enums.TipoVehiculo;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TarifaRequestDTO {
	
    @NotNull 
    private TipoVehiculo tipoVehiculo;
    
    @Positive 
    private Double valorHora;
    
    @Positive
    private Double valorDiaCompleto;
    
    @JsonFormat(pattern = "HH:mm") 
    @Schema(type = "string", example = "06:00")
    private LocalTime horaInicio;
    
    @JsonFormat(pattern = "HH:mm") 
    @Schema(type = "string", example = "18:00")
    private LocalTime horaFin;
    private Boolean esFestivo;
}