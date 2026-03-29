package com.cesde.parkingFlow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cesde.parkingFlow.entity.Tarifa;
import com.cesde.parkingFlow.enums.TipoVehiculo;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    
   
    List<Tarifa> findByTipoVehiculoAndActivoTrue(TipoVehiculo tipo);

    @Query("SELECT t FROM Tarifa t WHERE t.tipoVehiculo = :tipo AND t.activo = true " +
           "AND t.esFestivo = :esFestivo")
    List<Tarifa> buscarTarifasConflictivas(TipoVehiculo tipo, Boolean esFestivo);
    
    List<Tarifa> findByActivoTrue();
}