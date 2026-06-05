package com.cesde.parkingFlow.dto.response;

import com.cesde.parkingFlow.enums.EstadoSuscripcion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionResponseDTO {
    private Long id;
    private Long vehiculoId;
    private String placa;
    private Long planId;
    private String nombrePlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoSuscripcion estado;
    private Integer entriesUsadas; // Corregido de acuerdo a la propiedad de persistencia
    private String referenciaPago;
    
    // --- NUEVOS CAMPOS EXIGIDOS POR LA US-011 ---
    private Integer diasRestantes;
    private Boolean enParqueadero;
}