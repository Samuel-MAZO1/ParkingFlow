package com.cesde.parkingFlow.entity;

import java.time.LocalTime;



import com.cesde.parkingFlow.enums.TipoVehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tarifas")
@Getter @Setter
@NoArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "valor_hora", nullable = false)
    private Double valorHora;

    @Column(name = "valor_dia_completo")
    private Double valorDiaCompleto;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio; 

    @Column(name = "hora_fin")
    private LocalTime horaFin;   

    @Column(name = "es_festivo")
    private Boolean esFestivo = false;

    @Column(nullable = false)
    private Boolean activo = true;
}