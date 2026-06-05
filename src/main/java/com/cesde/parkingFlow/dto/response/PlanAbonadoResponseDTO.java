package com.cesde.parkingFlow.dto.response;

import com.cesde.parkingFlow.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanAbonadoResponseDTO {
    private Long id;
    private String nombre;
    private TipoVehiculo tipoVehiculo;
    private Double precioMensual;
    private Boolean entradasIlimitadas;
    private Integer maxEntradas;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private Boolean activo;
}