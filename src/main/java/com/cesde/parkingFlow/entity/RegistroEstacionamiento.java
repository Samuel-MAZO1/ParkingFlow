package com.cesde.parkingFlow.entity;

import com.cesde.parkingFlow.enums.EstadoRegistro;
import com.cesde.parkingFlow.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registros_estacionamiento")
@Builder
public class RegistroEstacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "hora_entrada", nullable = false)
    private LocalDateTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    @Column(name = "es_abonado", nullable = false)
    private Boolean esAbonado;

    @Column(name = "suscripcion_id")
    private Long suscripcionId; // Se asociará en la US-014, para ocasionales permanece null

    @Column(name = "monto_cobrado")
    private Double montoCobrado;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRegistro estado;

    @PrePersist
    protected void onCreate() {
        this.horaEntrada = LocalDateTime.now();
    }
}