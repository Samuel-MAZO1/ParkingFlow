package com.cesde.parkingFlow.repository;

import com.cesde.parkingFlow.entity.Suscripcion;
import com.cesde.parkingFlow.entity.Vehiculo;
import com.cesde.parkingFlow.enums.EstadoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    // Permite comprobar si el vehículo ya tiene contratos de suscripción vigentes
    boolean existsByVehiculoAndEstado(Vehiculo vehiculo, EstadoSuscripcion estado);
}