package com.cesde.parkingFlow.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionRequestDTO {

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;

    @NotNull(message = "El ID del plan de suscripción es obligatorio")
    private Long planId;

    @FutureOrPresent(message = "La fecha de inicio debe ser el día de hoy o una fecha futura")
    private LocalDate fechaInicio; // Si llega nula en la petición se asume fecha actual (Inmediata)

    @NotBlank(message = "La referencia de pago es obligatoria")
    private String referenciaPago;
}