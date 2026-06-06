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
public class TicketResponseDTO {
    private String numeroTicket; // Número único generado (Ejem: TKT-10023)
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private LocalDateTime horaEntrada;
    private EstadoRegistro estado;
    private Boolean esAbonado;
}