package com.cesde.parkingFlow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cesde.parkingFlow.entity.ConfiguracionCapacidad;
import com.cesde.parkingFlow.enums.TipoVehiculo;

@Repository
public interface ConfiguracionCapacidadRepository extends JpaRepository<ConfiguracionCapacidad, Long> {
    
    Optional<ConfiguracionCapacidad> findByTipoVehiculo(TipoVehiculo tipo);

    @Query("SELECT SUM(c.capacidadAsignada) FROM ConfiguracionCapacidad c WHERE c.tipoVehiculo != :tipo")
    Integer sumarAsignadoOtrosTipos(@Param("tipo") TipoVehiculo tipo);
}