package com.cesde.parkingFlow.dto.response;

import com.cesde.parkingFlow.enums.EstadoRegistro;
import com.cesde.parkingFlow.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecaudoResponseDTO {
    private String numeroTicket;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private Long minutosPermanencia;
    private Boolean esAbonado;
    private Double montoCobrado;
    private String metodoPago;
    private EstadoRegistro estado;
}