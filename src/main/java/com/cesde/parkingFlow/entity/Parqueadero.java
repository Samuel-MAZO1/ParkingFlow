package com.cesde.parkingFlow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parqueaderos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parqueadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(name = "capacidad_fisica_total", nullable = false)
    private Integer capacidadFisicaTotal;

    // Ocupación de toda la sede (suma de todos los tipos)
    @Column(name = "ocupacion_global")
    private Integer ocupacionGlobal = 0;
}