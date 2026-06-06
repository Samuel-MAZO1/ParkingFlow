package com.cesde.parkingFlow.entity;

import com.cesde.parkingFlow.enums.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suscripciones")
@Builder
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanAbonado planAbonado;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSuscripcion estado;

    @Column(nullable = false)
    private Integer entradasUsadas;

    @Column(nullable = false)
    private String referenciaPago;

    // --- NUEVO CAMPO PARA LA HISTORIA DE USUARIO 12 ---
    @Column(name = "motivo_cancelacion")
    private String motivoCancelacion;
}