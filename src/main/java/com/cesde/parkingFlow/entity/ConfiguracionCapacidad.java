package com.cesde.parkingFlow.entity;

import com.cesde.parkingFlow.enums.TipoVehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuracion_capacidad")
@Getter @Setter
@NoArgsConstructor
public class ConfiguracionCapacidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private Parqueadero parqueadero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", unique = true, nullable = false)
    private TipoVehiculo tipoVehiculo; // MOTO, CARRO, CAMIONETA

    @Column(name = "capacidad_asignada", nullable = false)
    private Integer capacidadAsignada;

    @Column(name = "ocupacion_actual")
    private Integer ocupacionActual = 0;
}