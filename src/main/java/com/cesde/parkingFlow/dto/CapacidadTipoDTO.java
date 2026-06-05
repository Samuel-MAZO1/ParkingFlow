package com.cesde.parkingFlow.dto;

import com.cesde.parkingFlow.enums.TipoVehiculo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapacidadTipoDTO {
    private TipoVehiculo tipo;
    private Integer capacidadAsignada; 
    private Integer ocupacionActual;   
    private Integer cuposLibres;       // capacidadAsignada - ocupacionActual
    private Integer minimoPermitido;   // IMPORTANTE: El Admin no puede bajar de aquí (es la ocupaciónActual)
    
   
    public Integer getCuposLibres() {
        return Math.max(0, capacidadAsignada - ocupacionActual);
    }
}