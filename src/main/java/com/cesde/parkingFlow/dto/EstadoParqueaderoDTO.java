package com.cesde.parkingFlow.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadoParqueaderoDTO {
    private String nombreParqueadero;
    private Integer capacidadFisicaTotal; 
    private Integer ocupacionGlobal;      // Suma total de vehículos adentro
    private Integer cuposFisicosLibres;   // Capacidad física - Ocupación global
    
    private List<CapacidadTipoDTO> detallePorTipo;
   
    private Integer capacidadPorAsignar; 
}