package com.cesde.parkingFlow.entity;

import com.cesde.parkingFlow.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "planes_abonado")
@Builder
public class PlanAbonado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "precio_mensual", nullable = false)
    private Double precioMensual;

    @Column(name = "entradas_ilimitadas", nullable = false)
    private Boolean entradasIlimitadas;

    @Column(name = "max_entradas")
    private Integer maxEntradas;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fin", nullable = false)
    private LocalTime horarioFin;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    protected void onCreate() {
        if (this.activo == null) {
            this.activo = true;
        }
    }
}