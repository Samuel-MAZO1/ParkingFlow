package com.cesde.parkingFlow.dto;

import com.cesde.parkingFlow.enums.TipoVehiculo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanAbonadoRequestDTO {

    @NotBlank(message = "El nombre del plan es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "El precio mensual es obligatorio")
    @Min(value = 0, message = "El precio mensual no puede ser un valor negativo")
    private Double precioMensual;

    @NotNull(message = "Debe especificar si el plan cuenta con entradas ilimitadas")
    private Boolean entradasIlimitadas;

    private Integer maxEntradas; // Validado dinámicamente en el Service si entradasIlimitadas es false

    @NotNull(message = "El horario de inicio permitido es obligatorio")
    private LocalTime horarioInicio;

    @NotNull(message = "El horario de fin permitido es obligatorio")
    private LocalTime horarioFin;
}